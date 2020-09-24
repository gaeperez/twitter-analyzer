package es.uvigo.ei.sing.twitteranalyzer.controllers;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetEntity;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.*;
import es.uvigo.ei.sing.twitteranalyzer.utils.Constants;
import es.uvigo.ei.sing.twitteranalyzer.utils.Functions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Controller
public class AppController {

    private final TweetController tweetController;
    private final UserController userController;
    private final HashtagController hashtagController;
    private final MentionController mentionController;
    private final UrlController urlController;

    @Autowired
    public AppController(TweetController tweetController, UserController userController, HashtagController hashtagController,
                         MentionController mentionController, UrlController urlController) {
        this.tweetController = tweetController;
        this.userController = userController;
        this.hashtagController = hashtagController;
        this.mentionController = mentionController;
        this.urlController = urlController;
    }

    public void parseAllByRegionAndTopic(String region, String topic, boolean loadFile, int topNumber, float minNegative, float minPositive,
                                         boolean includeSuspended, int nGram, String... tokens) throws IOException {
        // Parse tweets information
        List<TweetJson> jsonTweets = parseTweetsByRegionAndTopic(region, topic, loadFile, topNumber, minNegative, minPositive, nGram, null, tokens);

        // Parse users information
        parseUsersByRegionAndTopic(region, topic, topNumber, includeSuspended, jsonTweets);

        // Parse hashtags information
        parseHashtagsByRegionAndTopic(region, topic, topNumber, jsonTweets);

        // Parse mentions information
        parseMentionsByRegionAndTopic(region, topic, topNumber, jsonTweets);

        // Parse URLs information
        parseUrlsByRegionAndTopic(region, topic, topNumber, jsonTweets);
    }

    public List<TweetJson> retrieveTweetsByRegionAndTopic(String region, String topic, boolean loadFile) throws IOException {
        List<TweetJson> jsonTweets;

        // Check if the tweets json file exists, if not put the variable to false and create it
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS);
        if (!Files.exists(savedPath))
            loadFile = false;

        if (loadFile) {
            log.info("Loading the tweets from path {}...", savedPath);
            jsonTweets = tweetController.readTweetJson(savedPath);
        } else {
            Files.deleteIfExists(savedPath);
            // Parse tweets information
            log.info("Finding tweets in database...");
            Set<TweetEntity> savedTweets = tweetController.findTweetsByRegionAndTopic(region, topic);
            log.debug("{} tweets has been founded...", savedTweets.size());
            log.info("Processing the tweets in JSON format...");
            jsonTweets = tweetController.writeTweetJson(savedTweets, savedPath);
        }

        return jsonTweets;
    }

    public List<TweetJson> parseTweetsByRegionAndTopic(String region, String topic, boolean loadFile, int topNumber, float minNegative,
                                                       float minPositive, int nGram, List<TweetJson> jsonTweets, String... tokens) throws IOException {
        // If the user do not pass an input list of tweets, get them from database/file
        if (jsonTweets == null || jsonTweets.isEmpty())
            // Parse tweets information
            jsonTweets = retrieveTweetsByRegionAndTopic(region, topic, loadFile);

        // Get counts for tweets
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_COUNT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating counts for tweets...");
        Functions.writeJson(savedPath, tweetController.getTopCountByFields(jsonTweets, topNumber));
        // Get summary for tweets
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_SUMMARY);
        Files.deleteIfExists(savedPath);
        log.info("Calculating summary for tweets...");
        Functions.writeJson(savedPath, tweetController.getSummaryByFields(jsonTweets));
        // Get nGrams for tweets
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_NGRAMS);
        Files.deleteIfExists(savedPath);
        log.info("Calculating top n-grams for tweets...");
        Functions.writeJson(savedPath, tweetController.getTopLemmaNGrams(jsonTweets, topNumber));
        // Get dates for tweets
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_DATE);
        Files.deleteIfExists(savedPath);
        log.info("Calculating count by dates for tweets...");
        Functions.writeJson(savedPath, tweetController.countTweetsByDates(jsonTweets));
        // Get sentiments for tweets
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_SENTIMENT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating sentiment analysis for tweets...");
        Functions.writeJson(savedPath, tweetController.countTweetsBySentiment(jsonTweets, minNegative, minPositive));
        // Get join tokens for tweets
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_TWEETS_JOINTOK);
        Files.deleteIfExists(savedPath);
        Map<String, Map<String, Integer>> mapJoinsCount = new HashMap<>();
        for (String token : tokens) {
            log.info("Calculating join tokens for token {}...", token);
            mapJoinsCount.put(token, tweetController.getTopJoinTokens(jsonTweets, token, nGram, topNumber));
        }
        Functions.writeJson(savedPath, mapJoinsCount);

        return jsonTweets;
    }

    public List<UserJson> parseUsersByRegionAndTopic(String region, String topic, int topNumber, boolean includeSuspended,
                                                     List<TweetJson> jsonTweets) throws IOException {
        // Parse users information
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_USERS);
        Files.deleteIfExists(savedPath);
        log.info("Processing the users in JSON format...");
        List<UserJson> userJsons = userController.writeOrReadUserJson(jsonTweets, savedPath, includeSuspended);
        log.debug("{} users has been processed...", userJsons.size());
        // Get counts for users
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_USERS_COUNT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating top count fields for users...");
        Functions.writeJson(savedPath, userController.getTopCountField(userJsons, topNumber));
        // Get summary for users
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_USERS_SUMMARY);
        Files.deleteIfExists(savedPath);
        log.info("Calculating summary for users...");
        Functions.writeJson(savedPath, userController.getSummaryByFields(userJsons));

        return userJsons;
    }

    public List<HashtagJson> parseHashtagsByRegionAndTopic(String region, String topic, int topNumber,
                                                           List<TweetJson> jsonTweets) throws IOException {
        // Parse hashtags information
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_HTS);
        Files.deleteIfExists(savedPath);
        log.info("Processing the hashtags in JSON format...");
        List<HashtagJson> hashtagJsons = hashtagController.writeOrReadHashtagJson(jsonTweets, savedPath);
        log.debug("{} hashtags has been processed...", hashtagJsons.size());
        // Get counts for hashtags
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_HTS_COUNT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating top count fields for hashtags...");
        Functions.writeJson(savedPath, hashtagController.getTopCountField(hashtagJsons, topNumber));
        // Get summary for hashtags
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_HTS_SUMMARY);
        Files.deleteIfExists(savedPath);
        log.info("Calculating summary for hashtags...");
        Functions.writeJson(savedPath, hashtagController.getSummaryByFields(hashtagJsons));

        return hashtagJsons;
    }

    public List<MentionJson> parseMentionsByRegionAndTopic(String region, String topic, int topNumber,
                                                           List<TweetJson> jsonTweets) throws IOException {
        // Parse mentions information
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_MENTIONS);
        Files.deleteIfExists(savedPath);
        log.info("Processing the mentions in JSON format...");
        List<MentionJson> mentionJsons = mentionController.writeOrReadMentionJson(jsonTweets, savedPath);
        log.debug("{} mentions has been processed...", mentionJsons.size());
        // Get counts for mentions
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_MENTIONS_COUNT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating top count fields for mentions...");
        Functions.writeJson(savedPath, mentionController.getTopCountField(mentionJsons, topNumber));
        // Get summary for mentions
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_MENTIONS_SUMMARY);
        Files.deleteIfExists(savedPath);
        log.info("Calculating summary for mentions...");
        Functions.writeJson(savedPath, mentionController.getSummaryByFields(mentionJsons));

        return mentionJsons;
    }

    public List<UrlDomainJson> parseUrlsByRegionAndTopic(String region, String topic, int topNumber,
                                                         List<TweetJson> jsonTweets) throws IOException {
        // Parse URLs information
        Path savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_URLS);
        Files.deleteIfExists(savedPath);
        log.info("Processing the URLs in JSON format...");
        List<UrlDomainJson> domainJsons = urlController.writeOrReadUrlJson(jsonTweets, savedPath);
        log.debug("{} URLs has been processed...", domainJsons.size());
        // Get counts for URLs
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_URLS_COUNT);
        Files.deleteIfExists(savedPath);
        log.info("Calculating top count fields for URLs...");
        Functions.writeJson(savedPath, urlController.getTopCountField(domainJsons, topNumber));
        // Get summary for URLs
        savedPath = Paths.get(Constants.RES + region + "_" + topic + Constants.RES_URLS_SUMMARY);
        Files.deleteIfExists(savedPath);
        log.info("Calculating summary for URLs...");
        Functions.writeJson(savedPath, urlController.getSummaryByFields(domainJsons));

        return domainJsons;
    }
}
