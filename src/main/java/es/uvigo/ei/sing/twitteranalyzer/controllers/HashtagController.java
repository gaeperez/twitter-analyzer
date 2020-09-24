package es.uvigo.ei.sing.twitteranalyzer.controllers;

import com.twitter.twittertext.Extractor;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.HashtagJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TweetJson;
import es.uvigo.ei.sing.twitteranalyzer.utils.Constants;
import es.uvigo.ei.sing.twitteranalyzer.utils.Functions;
import es.uvigo.ei.sing.twitteranalyzer.utils.SummaryStatistics;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Controller
public class HashtagController {

    public List<HashtagJson> writeOrReadHashtagJson(List<TweetJson> tweetJsons, Path savedPath) {
        List<HashtagJson> toRet = new ArrayList<>();
        Map<String, HashtagJson> mapCreatedHashtag = new HashMap<>();
        AtomicInteger hashtagId = new AtomicInteger(1);

        // Save the tweets in disk
        if (!Files.exists(savedPath)) {
            final Extractor extractor = new Extractor();
            tweetJsons.forEach(tweetJson -> {
                // Get tweet text
                // Force a lower case to avoid things like Douro and douro
                String text = tweetJson.getText().toLowerCase();
                // Get tweet ID
                Long tweetId = tweetJson.getId();

                // Extract hashtags from the text
                List<String> extractHashtags = extractor.extractHashtags(text);

                // Iterate hashtags
                for (String hashtag : extractHashtags) {
                    HashtagJson hashtagJson;
                    if (!mapCreatedHashtag.containsKey(hashtag)) {
                        // Create hashtag
                        hashtagJson = new HashtagJson();
                        hashtagJson.setId(hashtagId.getAndIncrement());
                        hashtagJson.setTag(hashtag);

                        mapCreatedHashtag.put(hashtag, hashtagJson);
                        toRet.add(hashtagJson);
                    } else
                        hashtagJson = mapCreatedHashtag.get(hashtag);

                    // Set hashtag information
                    hashtagJson.incrementNumberOfTweets();
                    hashtagJson.incrementNumberOfUniqueTweets(tweetId);
                    hashtagJson.getTweetIds().add(tweetId);
                }
            });

            // Convert to a json
            toRet.sort(Comparator.comparing(HashtagJson::getId));
            Functions.writeJson(savedPath, toRet);
        } else {
            // Read JSON if the file exists
            HashtagJson[] readJson = Functions.readJson(savedPath, HashtagJson[].class);
            if (readJson != null)
                toRet.addAll(Arrays.asList(readJson));
        }

        return toRet;
    }

    public Map<String, Map<String, Integer>> getTopCountField(List<HashtagJson> hashtagJsons, long topNumber, String... fields) {
        // Parallelize the operation
        Map<String, Map<String, Integer>> mapFieldAttributeCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapAttributeCount;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.HASHTAG_COUNT_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.HASHTAG_TWEET_COUNT:
                    mapAttributeCount = hashtagJsons.parallelStream()
                            .sorted(Comparator.comparing(HashtagJson::getNumberOfTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, hashtagJson) -> map.put(hashtagJson.getTag(),
                                    hashtagJson.getNumberOfTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.HASHTAG_TWEET_COUNT, mapAttributeCount);
                    break;
                case Constants.HASHTAG_UNIQUE_TWEET_COUNT:
                    mapAttributeCount = hashtagJsons.parallelStream()
                            .sorted(Comparator.comparing(HashtagJson::getNumberOfUniqueTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, hashtagJson) -> map.put(hashtagJson.getTag(),
                                    hashtagJson.getNumberOfUniqueTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.HASHTAG_UNIQUE_TWEET_COUNT, mapAttributeCount);
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        // Return the desired top fields keeping order
        return mapFieldAttributeCount;
    }

    public Map<String, SummaryStatistics> getSummaryByFields(List<HashtagJson> hashtagJsons, String... fields) {
        Map<String, SummaryStatistics> mapFieldSummary = new HashMap<>();
        DoubleSummaryStatistics summary;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.HASHTAG_SUMMARY_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.HASHTAG_TWEET_SUMMARY:
                    // Tweets marked as favourites
                    summary = hashtagJsons.parallelStream()
                            .map(HashtagJson::getNumberOfTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.HASHTAG_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.HASHTAG_UNIQUE_TWEET_SUMMARY:
                    // Tweets containing hashtags
                    summary = hashtagJsons.parallelStream()
                            .map(HashtagJson::getNumberOfUniqueTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.HASHTAG_UNIQUE_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        return mapFieldSummary;
    }
}
