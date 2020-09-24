package es.uvigo.ei.sing.twitteranalyzer.controllers;

import com.twitter.twittertext.Extractor;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
import es.uvigo.ei.sing.twitteranalyzer.entities.TweetEntity;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.ProcessedTextJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TokenJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TweetJson;
import es.uvigo.ei.sing.twitteranalyzer.services.TweetService;
import es.uvigo.ei.sing.twitteranalyzer.utils.Constants;
import es.uvigo.ei.sing.twitteranalyzer.utils.Functions;
import es.uvigo.ei.sing.twitteranalyzer.utils.SummaryStatistics;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Controller
public class TweetController {

    private final TweetService tweetService;
    private final StanfordCoreNLP pipeline;

    @Autowired
    public TweetController(TweetService tweetService, StanfordCoreNLP pipeline) {
        this.tweetService = tweetService;
        this.pipeline = pipeline;
    }

    // Region = porto, douro, others, all. Topic = doc, tourism.
    public Set<TweetEntity> findTweetsByRegionAndTopic(String region, String topic) {
        Set<TweetEntity> toRet = new HashSet<>();

        switch (region.toLowerCase()) {
            case Constants.REGION_ALL:
                toRet = tweetService.findAll();
                break;
            case Constants.REGION_PORTO:
                if (topic.equalsIgnoreCase(Constants.TOPIC_DOC))
                    toRet = tweetService.findAllPortoDoc();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_TOURISM))
                    toRet = tweetService.findAllPortoTourism();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_ALL)) {
                    toRet = tweetService.findAllEnglishPorto();
                } else
                    log.warn("An empty topic value {} was selected, an empty Set will be returned...", topic);
                break;
            case Constants.REGION_DOURO:
                if (topic.equalsIgnoreCase(Constants.TOPIC_DOC))
                    toRet = tweetService.findAllDouroDoc();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_TOURISM))
                    toRet = tweetService.findAllDouroTourism();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_ALL)) {
                    toRet = tweetService.findAllEnglishDouro();
                } else
                    log.warn("An empty topic value {} was selected, an empty Set will be returned...", topic);
                break;
            case Constants.REGION_OTHERS:
                if (topic.equalsIgnoreCase(Constants.TOPIC_DOC))
                    toRet = tweetService.findAllOthersDoc();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_TOURISM))
                    toRet = tweetService.findAllOthersTourism();
                else if (topic.equalsIgnoreCase(Constants.TOPIC_ALL)) {
                    toRet = tweetService.findAllEnglishOthers();
                } else
                    log.warn("An empty topic value {} was selected, an empty Set will be returned...", topic);
                break;
            default:
                log.error("Bad region value {}, an empty Set will be returned...", region);
                break;
        }

        return toRet;
    }

    public List<TweetJson> writeTweetJson(Set<TweetEntity> tweetEntities, Path savedPath) {
        List<TweetJson> toRet = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        // Save the tweets in disk
        final Extractor extractor = new Extractor();
        final SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        // Read stopWords list
        final List<String> stopWords = Functions.readStopWords();

        // Tweets cannot be duplicated by query
        tweetEntities.forEach(tweetEntity -> {
            // Map Entity to JSON
            TweetJson tweetJson = modelMapper.map(tweetEntity, TweetJson.class);

            String text = tweetJson.getText();
            // Step 1: Extract information from tweet
            // Force a whitespace between URLs (fix some tweets that have typos)
            text = text.replaceAll("(http[s]*:)", " $1");
            tweetJson.setText(text);
            // Remove tags from source field
            tweetJson.setSource(Functions.htmlToText(tweetJson.getSource()));
            // Force a lower case in hashtag to avoid things like Douro and douro
            tweetJson.setExtractedHashtags(extractor.extractHashtags(text.toLowerCase()));
            tweetJson.setExtractedUrls(extractor.extractURLs(text));
            tweetJson.setExtractedMentions(extractor.extractMentionedScreennames(text));
            String reply = extractor.extractReplyScreenname(text);
            if (reply != null)
                tweetJson.setReplyName(extractor.extractReplyScreenname(text));
            else
                tweetJson.setReplyName("");
            try {
                // Process sentiment using VADER (not need to pre-process the text)
                sentimentAnalyzer.setInputString(text);
                sentimentAnalyzer.setInputStringProperties();
                sentimentAnalyzer.analyze();
                tweetJson.setSentiment(sentimentAnalyzer.getPolarity().get("compound"));
            } catch (Exception e) {
                log.error("Error processing the sentiment of tweet {}. See error: {}", tweetEntity.getId(), e);
            }

            // Step 2: Process text
            // Remove hashtags
            List<String> hashtags = tweetJson.getExtractedHashtags();
            // Long hashtags first
            hashtags.sort(Comparator.comparing(String::length).reversed());
            for (String hashtag : hashtags)
                text = org.apache.commons.lang3.StringUtils.removeIgnoreCase(text, "#" + hashtag);
            // Remove URLs
            for (String url : tweetJson.getExtractedUrls())
                text = org.apache.commons.lang3.StringUtils.remove(text, url);
            // Remove mentions
            for (String mention : tweetJson.getExtractedMentions())
                text = org.apache.commons.lang3.StringUtils.remove(text, "@" + mention);
            // Remove reply
            text = org.apache.commons.lang3.StringUtils.remove(text, tweetJson.getReplyName());
            // Put text in lowercase
            text = text.toLowerCase();
            // Remove HTML tags
            text = Functions.htmlToText(text);
            // Remove extra whitespaces
            text = text.replaceAll("\\s+", " ");

            // Tokenize document, filter tokens by range, filter stop words and lemmatization
            // Create Stanford document and annotate it
            CoreDocument exampleDocument = new CoreDocument(text);
            pipeline.annotate(exampleDocument);

            // Access tokens from a CoreDocument, a token is represented by a CoreLabel
            StringBuilder processedText = new StringBuilder();
            List<TokenJson> tweetTokens = new ArrayList<>();
            Map<Integer, List<String>> mapNGrams = new HashMap<>();
            List<CoreSentence> sentences = exampleDocument.sentences();
            // Consider all tweet as one sentence
            for (CoreSentence sentence : sentences) {
                List<CoreLabel> tokens = sentence.tokens();

                // Iterate tokens to be processed
                String word;
                String lemma;
                TokenJson tokenJson;
                for (CoreLabel token : tokens) {
                    word = token.word();
                    if (!stopWords.contains(word) && word.length() > 2 && word.length() < 20) {
                        // Create token json
                        tokenJson = new TokenJson();
                        tokenJson.setToken(token.word());
                        // Get token lemma
                        lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                        tokenJson.setLemma(lemma);
                        tokenJson.setPos(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                        tokenJson.setInit(token.beginPosition());
                        tokenJson.setEnd(token.endPosition());
                        tweetTokens.add(tokenJson);

                        // Construct the processed text
                        processedText.append(lemma).append(" ");
                    }
                }
            }

            // Get n-grams from bi-grams to X-grams from processed text (lemmatized)
            Collection<String> nGrams = StringUtils.getNgramsString(processedText.toString().trim(), 2, 3);
            long count;
            for (String nGram : nGrams) {
                // Count number of whitespaces to indicate which type of n-gram is (i.e. bi-gram, tri-gram, etc.)
                count = nGram.chars().filter(ch -> ch == ' ').count() + 1;
                mapNGrams.computeIfAbsent((int) count, v -> new ArrayList<>()).add(nGram);
            }

            // Remove extra whitespaces
            text = processedText.toString().trim();

            // Set processed text json
            ProcessedTextJson processedTextJson = new ProcessedTextJson();
            processedTextJson.setProcessedText(text);
            processedTextJson.setTokens(tweetTokens);
            processedTextJson.setLemmatizedNGrams(mapNGrams);
            tweetJson.setProcessedTextJson(processedTextJson);

            toRet.add(tweetJson);
        });

        // Sort JSON
        toRet.sort(Comparator.comparing(TweetJson::getId));
        try {
            Files.deleteIfExists(savedPath);
            Functions.writeJson(savedPath, toRet);
        } catch (IOException e) {
            log.error("Error while saving tweets file...");
        }

        return toRet;
    }

    public List<TweetJson> readTweetJson(Path savedPath) {
        List<TweetJson> toRet = new ArrayList<>();

        // Read tweets from disk
        if (Files.exists(savedPath)) {
            // Read JSON if the file exists
            TweetJson[] readJson = Functions.readJson(savedPath, TweetJson[].class);
            if (readJson != null)
                toRet.addAll(Arrays.asList(readJson));
        }

        return toRet;
    }

    /**
     * @param jsonTweets list with tweets converted in json format
     * @param topNumber  indicates the number of values to be returned (i.e. limit)
     * @param nGrams     indicates the type of n-grams to be returned (e.x. 1 = tokens, 2 = bi-grams, 3 = tri-grams, etc.)
     * @return map with {fieldName, {attribute, count}} (e.x. {1, {wine, 21}})
     */
    public Map<String, Map<String, Integer>> getTopLemmaNGrams(List<TweetJson> jsonTweets, long topNumber, int...
            nGrams) {
        // Parallelize the operation
        Map<String, Map<String, Integer>> mapFieldAttributeCount = new HashMap<>();

        if (nGrams.length == 0) {
            log.warn("No n-grams selected, retrieving information for all possible fields...");
            // Get all possible values
            nGrams = IntStream.range(1, 6).toArray();
        }

        for (int nGram : nGrams) {
            Map<String, Integer> mapNGramCount = Collections.synchronizedMap(new HashMap<>());

            if (nGram == 1) {
                // Add tokens to map, counting each one
                jsonTweets.parallelStream()
                        .map(TweetJson::getProcessedTextJson)
                        .map(ProcessedTextJson::getTokens)
                        .flatMap(List::stream)
                        .forEach(tokenJson -> mapNGramCount.merge(tokenJson.getLemma(), 1, Integer::sum));
            } else {
                // Add tokens to map, counting each one
                jsonTweets.parallelStream()
                        .map(TweetJson::getProcessedTextJson)
                        .map(ProcessedTextJson::getLemmatizedNGrams) // Get Map with NGrams
                        .filter(map -> map.containsKey(nGram)) // Get only maps with the desired n-gram
                        .map(map -> map.get(nGram)) // Get the desired NGram (e.g. bi-gram)
                        .flatMap(List::stream) // Convert the Stream into a list
                        .forEach(mapNGram -> mapNGramCount.merge(mapNGram, 1, Integer::sum));
            }
            if (!mapNGramCount.isEmpty())
                // Add values to returned map (sort and limit the inner map here)
                mapFieldAttributeCount.put(String.valueOf(nGram),
                        mapNGramCount.entrySet()
                                .parallelStream()
                                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Order by count desc
                                .limit(topNumber)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
        }

        // Sort map by value
        return mapFieldAttributeCount;
    }

    /**
     * Discover the most joined tokens with the input word.
     *
     * @param jsonTweets list with tweets converted in json format
     * @param word       reference word (e.x. douro)
     * @param nGram      indicates the type of n-grams to be searched (e.x. 2 = bi-grams, 3 = tri-grams, etc.). Must be greater than 1
     * @param posTags    indicates the possible values of the pos tags of the joined words. Can be empty to consider all types of pos
     * @param topNumber  indicates the number of values to be returned (i.e. limit)
     * @return map with {joinTokens, count} (e.x. {douro beautiful, 5})
     */
    public Map<String, Integer> getTopJoinTokens(List<TweetJson> jsonTweets, String word, int nGram,
                                                 long topNumber, String... posTags) {
        // Parallelize the operation
        Map<String, Integer> mapJoinCount = Collections.synchronizedMap(new HashMap<>());

        if (nGram > 1) {
            // Get tokens based on the input pos tags (if list is empty, get all tokens)
            Set<String> tokensToCompare = jsonTweets.parallelStream()
                    .map(TweetJson::getProcessedTextJson)
                    .map(ProcessedTextJson::getTokens)
                    .flatMap(List::stream)
                    .filter(token -> {
                        boolean toRet = false;

                        if (posTags.length == 0)
                            toRet = true;
                        else {
                            // E.g. token has pos = VBN and the input pos is VB.*
                            for (String posTag : posTags) {
                                if (token.getPos().matches(posTag)) {
                                    toRet = true;
                                    break;
                                }
                            }
                        }

                        return toRet;
                    })
                    .map(TokenJson::getLemma)
                    .collect(Collectors.toSet());

            // Add tokens to map, counting each one
            jsonTweets.stream()
                    .map(TweetJson::getProcessedTextJson)
                    .map(ProcessedTextJson::getLemmatizedNGrams) // Get Map with n-grams
                    .filter(map -> map.containsKey(nGram)) // Get only maps with the desired n-gram
                    .map(map -> map.get(nGram)) // Get n-grams
                    .flatMap(List::stream) // Convert the Stream into a list
                    .filter(grams -> grams.matches(".*\\b" + word + "\\b.*")) // The nGram must contain the input word
                    .map(grams -> grams.split(" ")) // Split n-grams by whitespace
                    .filter(grams -> {
                        boolean toRet = false;

                        // There are no POS tags, so consider the nGram
                        if (tokensToCompare.isEmpty()) {
                            toRet = true;
                        } else {
                            // Otherwise, check if the join tokens are allowed by their POS tags
                            // All grams except the input one must be contained in the tokensToCompare set
                            int count = 0;
                            for (String gram : grams) {
                                if (!gram.equalsIgnoreCase(word) && tokensToCompare.contains(gram))
                                    count++;
                            }

                            if (count == grams.length - 1)
                                toRet = true;
                        }

                        return toRet;
                    })
                    .map(grams -> String.join(" ", grams))
                    .forEach(grams -> mapJoinCount.merge(grams, 1, Integer::sum));
        } else
            log.warn("The n-gram value must be greater than 1");


        // Sort map by value
        return mapJoinCount.entrySet()
                .parallelStream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Order by count desc
                .limit(topNumber)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * @param jsonTweets list with tweets converted in json format
     * @param topNumber  indicates the number of values to be returned (i.e. limit)
     * @param field      indicates the field to be searched (e.x. favourite or hashtag)
     * @return list with tweets converted in json format and filtered by the input field
     */
    public List<TweetJson> getTopTweetsByField(List<TweetJson> jsonTweets, long topNumber, String field) {
        // Parallelize the operation
        List<TweetJson> toRet = new ArrayList<>();

        switch (field) {
            case Constants.TWEET_FAV_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getFavouriteCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.TWEET_HASHTAG_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getHashtagCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.TWEET_MEDIA_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getMediaCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.TWEET_MENTION_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getMentionsCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.TWEET_RT_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getRetweetCount).reversed())
                        .limit(topNumber)
                        .collect(Collectors.toList());
                break;
            case Constants.TWEET_URL_COUNT:
                toRet = jsonTweets.parallelStream()
                        .sorted(Comparator.comparing(TweetJson::getUrlCount).reversed())
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

    /**
     * @param jsonTweets list with tweets converted in json format
     * @param topNumber  indicates the number of values to be returned (i.e. limit)
     * @param fields     indicates the fields to be searched (e.x. favourite or hashtag)
     * @return map with {fieldName, {attribute, count}} (e.x. {tweet_favourite_count, {This is an example text, 42}})
     */
    public Map<String, Map<String, Integer>> getTopCountByFields(List<TweetJson> jsonTweets,
                                                                 long topNumber, String... fields) {
        // Parallelize the operation
        Map<String, Map<String, Integer>> mapFieldAttributeCount = new HashMap<>();
        Map<String, Integer> mapAttributeCount;
        long count;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.TWEET_COUNT_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.TWEET_FAV_COUNT:
                    // Tweets marked as favourites
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getFavouriteCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getFavouriteCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_FAV_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_HASHTAG_COUNT:
                    // Tweets containing hashtags
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getHashtagCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getHashtagCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_HASHTAG_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_MEDIA_COUNT:
                    // Tweets containing media
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getMediaCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getMediaCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_MEDIA_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_MENTION_COUNT:
                    // Tweets mentioning people
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getMentionsCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getMentionsCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_MENTION_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_RT_COUNT:
                    // Tweets marked as retweets
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getRetweetCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getRetweetCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_RT_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_URL_COUNT:
                    // Tweets containing URLs
                    mapAttributeCount = jsonTweets.parallelStream()
                            .sorted(Comparator.comparing(TweetJson::getUrlCount).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, tweetJson) -> map.put(tweetJson.getText(),
                                    tweetJson.getUrlCount()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.TWEET_URL_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_REPLY_COUNT:
                    // Tweets replying someone
                    mapAttributeCount = new HashMap<>();
                    // Get all distinct replies
                    Set<String> replies = jsonTweets.parallelStream().map(TweetJson::getReplyName)
                            .filter(reply -> !reply.isEmpty()).collect(Collectors.toSet());
                    for (String reply : replies) {
                        // Count tweets for current sources
                        count = jsonTweets.parallelStream().map(TweetJson::getReplyName)
                                .filter(replyTweet -> replyTweet != null && replyTweet.equalsIgnoreCase(reply)).count();
                        mapAttributeCount.put(reply, Math.toIntExact(count));
                    }
                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .parallelStream()
                            .limit(topNumber)
                            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Order by count desc
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    mapFieldAttributeCount.put(Constants.TWEET_REPLY_COUNT, mapAttributeCount);
                    break;
                case Constants.TWEET_SOURCE_COUNT:
                    // The source of the tweet (i.e. application or device used to post the tweet)
                    mapAttributeCount = new HashMap<>();
                    // Get all distinct sources
                    Set<String> sources = jsonTweets.parallelStream().map(TweetJson::getSource)
                            .filter(source -> source != null && !source.isEmpty()).collect(Collectors.toSet());
                    for (String source : sources) {
                        // Count tweets for current sources
                        count = jsonTweets.parallelStream().map(TweetJson::getSource)
                                .filter(tweetSource -> tweetSource != null && tweetSource.equalsIgnoreCase(source)).count();
                        mapAttributeCount.put(source, Math.toIntExact(count));
                    }
                    // Sort map
                    mapAttributeCount = mapAttributeCount.entrySet()
                            .parallelStream()
                            .limit(topNumber)
                            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Order by count desc
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    mapFieldAttributeCount.put(Constants.TWEET_SOURCE_COUNT, mapAttributeCount);
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        // Return the desired top fields keeping order
        return mapFieldAttributeCount;
    }

    /**
     * Get tweets based on a range of dates
     *
     * @param jsonTweets list with tweets converted in json format
     * @param since      First date to start searching tweets
     * @param until      Last date to search tweets
     * @return list with tweets converted in json format and filtered by the input range values
     */
    public List<TweetJson> getTweetsByDateRange(List<TweetJson> jsonTweets, LocalDateTime since, LocalDateTime
            until) {
        return jsonTweets.stream()
                .filter(tweetJson -> tweetJson.getCreated().isAfter(since) && tweetJson.getCreated().isBefore(until))
                .sorted(Comparator.comparing(TweetJson::getCreated))
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Integer>> countTweetsByDates(List<TweetJson> jsonTweets) {
        // Map to make the counts by year, month, day of the week and hour of the day
        Map<String, Integer> mapYearCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapMonthCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapDayCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapHourCount = Collections.synchronizedMap(new HashMap<>());

        jsonTweets.parallelStream().map(TweetJson::getCreated).forEach(jsonTweet -> {
            // Count the year
            mapYearCount.merge(String.valueOf(jsonTweet.getYear()), 1, Integer::sum);
            // Count the month
            mapMonthCount.merge(jsonTweet.getMonth().name(), 1, Integer::sum);
            // Count the day of the week
            mapDayCount.merge(jsonTweet.getDayOfWeek().name(), 1, Integer::sum);
            // Count the hour of the day
            mapHourCount.merge(String.valueOf(jsonTweet.getHour()), 1, Integer::sum);
        });

        // Map to return
        Map<String, Map<String, Integer>> toRet = new HashMap<>();
        toRet.put(Constants.DATE_YEAR, mapYearCount);
        toRet.put(Constants.DATE_MONTH, mapMonthCount);
        toRet.put(Constants.DATE_DAY, mapDayCount);
        toRet.put(Constants.DATE_HOUR, mapHourCount);

        return toRet;
    }

    public List<TweetJson> getTweetsBySentiment(List<TweetJson> jsonTweets, float minCompound, float maxCompound) {
        // Filter tweets by compound range
        return jsonTweets.stream()
                .filter(tweetJson -> tweetJson.getSentiment() >= minCompound && tweetJson.getSentiment() <= maxCompound)
                .sorted(Comparator.comparing(TweetJson::getSentiment))
                .collect(Collectors.toList());
    }

    public Map<String, Long> countTweetsBySentiment(List<TweetJson> jsonTweets, float minNegative,
                                                    float minPositive) {
        // Map to make the counts by year, month, day of the week and hour of the day
        Map<String, Long> mapSentimentCount = Collections.synchronizedMap(new HashMap<>());

        // Count negative tweets
        long count = jsonTweets.parallelStream().map(TweetJson::getSentiment).filter(compound -> compound <= minNegative).count();
        mapSentimentCount.put(Constants.TWEET_NEGATIVE, count);

        // Count neutral tweets
        count = jsonTweets.parallelStream().map(TweetJson::getSentiment).filter(compound -> compound > minNegative && compound < minPositive).count();
        mapSentimentCount.put(Constants.TWEET_NEUTRAL, count);

        // Count positive tweets
        count = jsonTweets.parallelStream().map(TweetJson::getSentiment).filter(compound -> compound >= minPositive).count();
        mapSentimentCount.put(Constants.TWEET_POSITIVE, count);

        return mapSentimentCount;
    }

    public Map<String, SummaryStatistics> getSummaryByFields(List<TweetJson> jsonTweets, String... fields) {
        Map<String, SummaryStatistics> mapFieldSummary = new HashMap<>();
        DoubleSummaryStatistics summary;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.TWEET_SUMMARY_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.TWEET_FAV_SUMMARY:
                    // Tweets marked as favourites
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getFavouriteCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_FAV_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_HASHTAG_SUMMARY:
                    // Tweets containing hashtags
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getHashtagCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_HASHTAG_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_MEDIA_SUMMARY:
                    // Tweets containing media
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getMediaCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_MEDIA_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_MENTION_SUMMARY:
                    // Tweets mentioning people
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getMentionsCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_MENTION_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_RT_SUMMARY:
                    // Tweets marked as retweets
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getRetweetCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_RT_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_URL_SUMMARY:
                    // Tweets containing URLs
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getUrlCount)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_URL_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_CREATED_SUMMARY:
                    // Created date of tweets by year, month, day and hour
                    summary = jsonTweets.parallelStream()
                            .map(tweetJson -> tweetJson.getCreated().getYear())
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DATE_YEAR + "_" + Constants.TWEET_CREATED_SUMMARY, new SummaryStatistics(summary));
                    summary = jsonTweets.parallelStream()
                            .map(tweetJson -> tweetJson.getCreated().getMonthValue())
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DATE_MONTH + "_" + Constants.TWEET_CREATED_SUMMARY, new SummaryStatistics(summary));
                    summary = jsonTweets.parallelStream()
                            .map(tweetJson -> tweetJson.getCreated().getDayOfWeek().getValue())
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DATE_DAY + "_" + Constants.TWEET_CREATED_SUMMARY, new SummaryStatistics(summary));
                    summary = jsonTweets.parallelStream()
                            .map(tweetJson -> tweetJson.getCreated().getHour())
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DATE_HOUR + "_" + Constants.TWEET_CREATED_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.TWEET_SENTIMENT_SUMMARY:
                    // The sentiment of the tweet ranging from -1 to 1
                    summary = jsonTweets.parallelStream()
                            .map(TweetJson::getSentiment)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.TWEET_SENTIMENT_SUMMARY, new SummaryStatistics(summary));
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        return mapFieldSummary;
    }
}
