package es.uvigo.ei.sing.twitteranalyzer.controllers;

import es.uvigo.ei.sing.twitteranalyzer.entities.UserEntity;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TweetJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.UserJson;
import es.uvigo.ei.sing.twitteranalyzer.services.UserKnowledgeService;
import es.uvigo.ei.sing.twitteranalyzer.services.UserService;
import es.uvigo.ei.sing.twitteranalyzer.utils.Constants;
import es.uvigo.ei.sing.twitteranalyzer.utils.Functions;
import es.uvigo.ei.sing.twitteranalyzer.utils.SummaryStatistics;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class UserController {

    private final UserService userService;
    private final UserKnowledgeService userKnowledgeService;

    @Autowired
    public UserController(UserService userService, UserKnowledgeService userKnowledgeService) {
        this.userService = userService;
        this.userKnowledgeService = userKnowledgeService;
    }

    public List<UserJson> writeOrReadUserJson(List<TweetJson> tweetJsons, Path savedPath, boolean includeSuspended) {
        List<UserJson> toRet = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        Map<Long, UserJson> mapCreatedUser = new HashMap<>();

        // Save the tweets in disk
        if (!Files.exists(savedPath)) {
            // Add users with their tweets in each specific topic (i.e. douro, porto or others)
            Map<UserEntity, List<TweetJson>> mapUserTweets = new HashMap<>();
            tweetJsons.forEach(tweetJson -> {
                // Get user id
                Long userId = tweetJson.getUserId();
                // Get the associated user from database
                Optional<UserEntity> possibleUser = userService.findById(userId);

                if (possibleUser.isPresent()) {
                    UserEntity userEntity = possibleUser.get();

                    // Ignore suspended users
                    if (includeSuspended || !userEntity.getSuspended()) {
                        // Ignore users without information
                        if (!userEntity.getName().equalsIgnoreCase("not retrieved for now")) {
                            // Add user and tweets to the map
                            mapUserTweets.computeIfAbsent(userEntity, k -> new ArrayList<>()).add(tweetJson);
                        }
                    }
                }
            });

            // Create UserJsons from UserEntities (users are unique)
            for (UserEntity userEntity : mapUserTweets.keySet()) {
                // Get user id
                Long userId = userEntity.getId();

                // Map Entity to Json class
                UserJson userJson = modelMapper.map(userEntity, UserJson.class);

                // Get the tweets of the user that are related to the specific topic (e.g. douro)
                List<TweetJson> userTweets = mapUserTweets.get(userEntity);
                int favCount = 0, rtCount = 0, tweetsCount = userTweets.size();
                if (tweetsCount > 0) {
                    // Iterate all user tweets in the topic
                    for (TweetJson userTweet : userTweets) {
                        favCount += userTweet.getFavouriteCount();
                        rtCount += userTweet.getRetweetCount();
                    }

                    // Add custom information related to the topic under study
                    userJson.setRtsCountInTopic(rtCount);
                    userJson.setFavouritesCountInTopic(favCount);
                    userJson.setStatusesCountInTopic(tweetsCount);
                    userJson.setRtRatio((double) rtCount / (double) tweetsCount);
                    userJson.setFavRatio((double) favCount / (double) tweetsCount);
                }
                int friendsCount = userJson.getFriendsCount();
                if (friendsCount > 0)
                    userJson.setFfRatio((double) userJson.getFollowersCount() / (double) userJson.getFriendsCount());

                // Save user in map
                mapCreatedUser.put(userId, userJson);
                toRet.add(userJson);
            }

            // Sort by ID
            toRet.sort(Comparator.comparing(UserJson::getId));
            Functions.writeJson(savedPath, toRet);
        } else {
            // Read JSON if the file exists
            UserJson[] readJson = Functions.readJson(savedPath, UserJson[].class);
            if (readJson != null) {
                List<UserJson> savedUsers = Arrays.asList(readJson);
                // Ignore suspended users
                toRet.addAll(savedUsers.stream().filter(userJson -> {
                    boolean isValid = true;

                    if (!includeSuspended && userJson.getSuspended())
                        isValid = false;

                    return isValid;
                }).collect(Collectors.toList()));
            }
        }

        return toRet;
    }

    /**
     * @param jsonUsers list with the users in json format
     * @param topNumber indicates the number of values to be returned (i.e. limit)
     * @param fields    indicates the fields to be searched (e.x. favourite or hashtag)
     * @return map with {fieldName, {attribute, count}} (e.x. {user_followers_count, {pepito, 7354}})
     */
    public Map<String, Map<String, Object>> getTopCountField(List<UserJson> jsonUsers, long topNumber, String... fields) {
        // Parallelize the operation
        Map<String, Map<String, Object>> mapFieldAttributeCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Object> mapAttributeCount;
        Set<String> databaseValues;
        long count;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.USER_COUNT_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.USER_FOLLOWERS_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFollowersCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFollowersCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FOLLOWERS_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_FRIENDS_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFriendsCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFriendsCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FRIENDS_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_STATUSES_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getStatusesCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getStatusesCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_STATUSES_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_STATUSES_TOPIC_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getStatusesCountInTopic).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getStatusesCountInTopic()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_STATUSES_TOPIC_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_FAVOURITES_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFavouritesCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFavouritesCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FAVOURITES_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_FAVOURITES_TOPIC_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFavouritesCountInTopic).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFavouritesCountInTopic()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FAVOURITES_TOPIC_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_LISTED_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getListedCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getListedCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_LISTED_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_FF_RATIO_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFfRatio).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFfRatio()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FF_RATIO_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_RT_RATIO_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getRtRatio).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getRtRatio()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_RT_RATIO_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_FAV_RATIO_COUNT:
                    mapAttributeCount = jsonUsers.parallelStream()
                            .sorted(Comparator.comparing(UserJson::getFavRatio).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, userJson) -> map.put(userJson.getScreenName(),
                                    userJson.getFavRatio()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.USER_FAV_RATIO_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_SEX_COUNT:
                    // Get all possible values from database
                    databaseValues = userKnowledgeService.findDistinctSex();

                    // Count distinct sex
                    mapAttributeCount = new HashMap<>();
                    for (String sex : databaseValues) {
                        // Count occurrences for current sex value
                        count = jsonUsers.parallelStream().map(UserJson::getUserKnowledgeEntitySex)
                                .filter(userSex -> userSex != null && userSex.equalsIgnoreCase(sex)).count();
                        if (count > 0L)
                            mapAttributeCount.put(sex, count);
                    }

                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(o -> (long) o.getValue(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

                    mapFieldAttributeCount.put(Constants.USER_SEX_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_CLASSIFICATION_COUNT:
                    // Get all possible values from database
                    databaseValues = userKnowledgeService.findDistinctClassifications();

                    // Count distinct classifications
                    mapAttributeCount = new HashMap<>();
                    for (String classification : databaseValues) {
                        // Count occurrences for current classification value
                        count = jsonUsers.parallelStream().map(UserJson::getUserKnowledgeEntityClassification)
                                .filter(userClass -> userClass != null && userClass.equalsIgnoreCase(classification)).count();
                        if (count > 0L)
                            mapAttributeCount.put(classification, count);
                    }

                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(o -> (long) o.getValue(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

                    mapFieldAttributeCount.put(Constants.USER_CLASSIFICATION_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_ROLE_COUNT:
                    // Get all possible values from database
                    databaseValues = userKnowledgeService.findDistinctRoles();

                    // Count distinct roles
                    mapAttributeCount = new HashMap<>();
                    for (String role : databaseValues) {
                        // Count occurrences for current role value
                        count = jsonUsers.parallelStream().map(UserJson::getUserKnowledgeEntityRole)
                                .filter(userRole -> userRole != null && userRole.equalsIgnoreCase(role)).count();
                        if (count > 0L)
                            mapAttributeCount.put(role, count);
                    }

                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(o -> (long) o.getValue(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

                    mapFieldAttributeCount.put(Constants.USER_ROLE_COUNT, mapAttributeCount);
                    break;
                case Constants.USER_COUNTRY_COUNT:
                    // Get all possible values from database
                    databaseValues = userKnowledgeService.findDistinctCountries();

                    // Count distinct countries
                    mapAttributeCount = new HashMap<>();
                    for (String country : databaseValues) {
                        // Count occurrences for current country value
                        count = jsonUsers.parallelStream().map(UserJson::getUserKnowledgeEntityCountry)
                                .filter(userCountry -> userCountry != null && userCountry.equalsIgnoreCase(country)).count();
                        if (count > 0L)
                            mapAttributeCount.put(country, count);
                    }

                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(o -> (long) o.getValue(), Comparator.reverseOrder()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

                    mapFieldAttributeCount.put(Constants.USER_COUNTRY_COUNT, mapAttributeCount);
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        // Return the desired top fields keeping order
        return mapFieldAttributeCount;
    }

    public List<UserJson> getTopUsersByField(List<UserJson> jsonUsers, long topNumber, String field) {
        // Parallelize the operation
        List<UserJson> toRet = new ArrayList<>();

        switch (field) {
            case Constants.USER_FOLLOWERS_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFollowersCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_FRIENDS_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFriendsCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_STATUSES_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getStatusesCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_STATUSES_TOPIC_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getStatusesCountInTopic).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_FAVOURITES_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFavouritesCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_FAVOURITES_TOPIC_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFavouritesCountInTopic).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_LISTED_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getListedCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_FF_RATIO_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFfRatio).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_RT_RATIO_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getRtRatio).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.USER_FAV_RATIO_COUNT:
                toRet = jsonUsers.parallelStream()
                        .sorted(Comparator.comparing(UserJson::getFavRatio).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            default:
                log.warn("Invalid field {}", field);
                break;
        }

        // Return the desired top fields keeping order
        return toRet;
    }

    public List<UserJson> getUsersByKnowledgeField(List<UserJson> jsonUsers, String field, String... values) {
        List<UserJson> toRet = new ArrayList<>();

        if (values.length != 0) {
            switch (field) {
                case Constants.USER_SEX_COUNT:
                    toRet = jsonUsers.stream()
                            .filter(userJson -> {
                                boolean isValid = false;
                                for (String sex : values) {
                                    if (userJson.getUserKnowledgeEntitySex().equalsIgnoreCase(sex)) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                return isValid;
                            })
                            .sorted(Comparator.comparing(UserJson::getUserKnowledgeEntitySex))
                            .collect(Collectors.toList());
                    break;
                case Constants.USER_CLASSIFICATION_COUNT:
                    toRet = jsonUsers.stream()
                            .filter(userJson -> {
                                boolean isValid = false;
                                for (String classification : values) {
                                    if (userJson.getUserKnowledgeEntityClassification().equalsIgnoreCase(classification)) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                return isValid;
                            })
                            .sorted(Comparator.comparing(UserJson::getUserKnowledgeEntityClassification))
                            .collect(Collectors.toList());
                    break;
                case Constants.USER_ROLE_COUNT:
                    toRet = jsonUsers.stream()
                            .filter(userJson -> {
                                boolean isValid = false;
                                for (String role : values) {
                                    if (userJson.getUserKnowledgeEntityRole().equalsIgnoreCase(role)) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                return isValid;
                            })
                            .sorted(Comparator.comparing(UserJson::getUserKnowledgeEntityRole))
                            .collect(Collectors.toList());
                    break;
                case Constants.USER_COUNTRY_COUNT:
                    toRet = jsonUsers.stream()
                            .filter(userJson -> {
                                boolean isValid = false;
                                for (String country : values) {
                                    if (userJson.getUserKnowledgeEntityCountry().equalsIgnoreCase(country)) {
                                        isValid = true;
                                        break;
                                    }
                                }
                                return isValid;
                            })
                            .sorted(Comparator.comparing(UserJson::getUserKnowledgeEntityCountry))
                            .collect(Collectors.toList());
                    break;
                default:
                    log.warn("Bad input field {}", field);
                    break;
            }
        } else
            log.warn("No values selected to apply a filter on the field {}", field);

        return toRet;
    }

    public Map<String, SummaryStatistics> getSummaryByFields(List<UserJson> userJsons, String... fields) {
        Map<String, SummaryStatistics> mapFieldSummary = new HashMap<>();
        DoubleSummaryStatistics summary;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.USER_SUMMARY_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.USER_FOLLOWERS_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFollowersCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FOLLOWERS_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_FRIENDS_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFriendsCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FRIENDS_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_STATUSES_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getStatusesCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_STATUSES_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_STATUSES_TOPIC_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getStatusesCountInTopic)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_STATUSES_TOPIC_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_FAVOURITES_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFavouritesCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FAVOURITES_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_FAVOURITES_TOPIC_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFavouritesCountInTopic)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FAVOURITES_TOPIC_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_LISTED_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getListedCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_LISTED_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_FF_RATIO_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFfRatio)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FF_RATIO_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_RT_RATIO_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getRtRatio)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_RT_RATIO_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.USER_FAV_RATIO_SUMMARY:
                    summary = userJsons.parallelStream()
                            .map(UserJson::getFavRatio)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.USER_FAV_RATIO_SUMMARY, new SummaryStatistics(summary));
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        return mapFieldSummary;
    }
}
