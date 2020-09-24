package es.uvigo.ei.sing.twitteranalyzer.controllers;

import com.twitter.twittertext.Extractor;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.MentionJson;
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
public class MentionController {

    public List<MentionJson> writeOrReadMentionJson(List<TweetJson> tweetJsons, Path savedPath) {
        List<MentionJson> toRet = new ArrayList<>();
        Map<String, MentionJson> mapCreatedMention = new HashMap<>();
        AtomicInteger mentionId = new AtomicInteger(1);

        // Save the tweets in disk
        if (!Files.exists(savedPath)) {
            final Extractor extractor = new Extractor();
            tweetJsons.forEach(tweetJson -> {
                // Get tweet text
                String text = tweetJson.getText();
                // Get tweet ID
                Long tweetId = tweetJson.getId();

                // Extract mentions from the text
                List<String> extractMentions = extractor.extractMentionedScreennames(text);

                // Iterate mentions
                for (String mention : extractMentions) {
                    MentionJson mentionJson;
                    if (!mapCreatedMention.containsKey(mention)) {
                        // Create mention
                        mentionJson = new MentionJson();
                        mentionJson.setId(mentionId.getAndIncrement());
                        mentionJson.setMention(mention);

                        mapCreatedMention.put(mention, mentionJson);
                        toRet.add(mentionJson);
                    } else
                        mentionJson = mapCreatedMention.get(mention);

                    // Set hashtag information
                    mentionJson.incrementNumberOfTweets();
                    mentionJson.incrementNumberOfUniqueTweets(tweetId);
                    mentionJson.getTweetIds().add(tweetId);
                }
            });

            // Convert to a json
            toRet.sort(Comparator.comparing(MentionJson::getId));
            Functions.writeJson(savedPath, toRet);
        } else {
            // Read JSON if the file exists
            MentionJson[] readJson = Functions.readJson(savedPath, MentionJson[].class);
            if (readJson != null)
                toRet.addAll(Arrays.asList(readJson));
        }

        return toRet;
    }

    public Map<String, Map<String, Integer>> getTopCountField(List<MentionJson> mentionJsons, long topNumber, String... fields) {
        // Parallelize the operation
        Map<String, Map<String, Integer>> mapFieldAttributeCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapAttributeCount;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.MENTION_COUNT_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.MENTION_TWEET_COUNT:
                    mapAttributeCount = mentionJsons.parallelStream()
                            .sorted(Comparator.comparing(MentionJson::getNumberOfTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, hashtagJson) -> map.put(hashtagJson.getMention(),
                                    hashtagJson.getNumberOfTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.MENTION_TWEET_COUNT, mapAttributeCount);
                    break;
                case Constants.MENTION_UNIQUE_TWEET_COUNT:
                    mapAttributeCount = mentionJsons.parallelStream()
                            .sorted(Comparator.comparing(MentionJson::getNumberOfUniqueTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, hashtagJson) -> map.put(hashtagJson.getMention(),
                                    hashtagJson.getNumberOfUniqueTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.MENTION_UNIQUE_TWEET_COUNT, mapAttributeCount);
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        // Return the desired top fields keeping order
        return mapFieldAttributeCount;
    }

    public Map<String, SummaryStatistics> getSummaryByFields(List<MentionJson> mentionJsons, String... fields) {
        Map<String, SummaryStatistics> mapFieldSummary = new HashMap<>();
        DoubleSummaryStatistics summary;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.MENTION_SUMMARY_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.MENTION_TWEET_SUMMARY:
                    // Tweets marked as favourites
                    summary = mentionJsons.parallelStream()
                            .map(MentionJson::getNumberOfTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.MENTION_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.MENTION_UNIQUE_TWEET_SUMMARY:
                    // Tweets containing hashtags
                    summary = mentionJsons.parallelStream()
                            .map(MentionJson::getNumberOfUniqueTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.MENTION_UNIQUE_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        return mapFieldSummary;
    }
}
