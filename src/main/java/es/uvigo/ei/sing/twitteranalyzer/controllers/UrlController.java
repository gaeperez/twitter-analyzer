package es.uvigo.ei.sing.twitteranalyzer.controllers;

import com.twitter.twittertext.Extractor;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TweetJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.UrlDomainJson;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.UrlJson;
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
public class UrlController {

    public List<UrlDomainJson> writeOrReadUrlJson(List<TweetJson> tweetJsons, Path savedPath) {
        List<UrlDomainJson> toRet = new ArrayList<>();
        Map<String, UrlJson> mapCreatedUrls = new HashMap<>();
        Map<String, UrlDomainJson> mapCreatedDomains = new HashMap<>();
        AtomicInteger urlId = new AtomicInteger(1);
        AtomicInteger domainId = new AtomicInteger(1);

        // Save the tweets in disk
        if (!Files.exists(savedPath)) {
            final Extractor extractor = new Extractor();
            tweetJsons.forEach(tweetJson -> {
                // Get tweet text
                String text = tweetJson.getText();
                // Get tweet ID
                Long tweetId = tweetJson.getId();

                // Extract URLs from the text
                List<String> extractURLs = extractor.extractURLs(text);

                // Iterate URLs
                for (String url : extractURLs) {
                    try {
                        // Step 1: Create or get the URL
                        // Resolve URL using JSoup
                        url = Functions.resolveUrl(url);

                        UrlJson urlJson;
                        if (!mapCreatedUrls.containsKey(url)) {
                            // Create url
                            urlJson = new UrlJson();
                            urlJson.setId(urlId.getAndIncrement());
                            urlJson.setUrl(url);

                            mapCreatedUrls.put(url, urlJson);
                        } else
                            urlJson = mapCreatedUrls.get(url);

                        // Step 2: Set information in the URL
                        urlJson.incrementNumberOfTweets();
                        urlJson.incrementNumberOfUniqueTweets(tweetId);
                        urlJson.getTweetIds().add(tweetId);

                        // Step 3: Create or retrieve the Domain
                        // Extract the domain name from the url
                        String domainName = Functions.getDomainName(url);

                        UrlDomainJson domainJson;
                        if (!mapCreatedDomains.containsKey(domainName)) {
                            // Create Domain
                            domainJson = new UrlDomainJson();
                            domainJson.setId(domainId.getAndIncrement());
                            domainJson.setDomain(domainName);

                            mapCreatedDomains.put(domainName, domainJson);
                            toRet.add(domainJson);
                        } else
                            domainJson = mapCreatedDomains.get(domainName);

                        // Step 4: Set information in the Domain
                        domainJson.incrementNumberOfTweets();
                        domainJson.incrementNumberOfUniqueTweets(tweetId);
                        domainJson.getTweetIds().add(tweetId);
                        domainJson.addUrlIntoDomain(urlJson);
                    } catch (Exception e) {
                        log.error("Error parsing the URL {} for Tweet {}. See error: {}", url, tweetJson.getId(), e);
                    }
                }
            });

            // Convert to a json
            toRet.sort(Comparator.comparing(UrlDomainJson::getId));
            Functions.writeJson(savedPath, toRet);
        } else {
            // Read JSON if the file exists
            UrlDomainJson[] readJson = Functions.readJson(savedPath, UrlDomainJson[].class);
            if (readJson != null)
                toRet.addAll(Arrays.asList(readJson));
        }

        return toRet;
    }

    public Map<String, Map<String, Integer>> getTopCountField(List<UrlDomainJson> domainJsons, long topNumber, String... fields) {
        // Parallelize the operation
        Map<String, Map<String, Integer>> mapFieldAttributeCount = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> mapAttributeCount;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.URL_COUNT_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.DOMAIN_TWEET_COUNT:
                    mapAttributeCount = domainJsons.parallelStream()
                            .sorted(Comparator.comparing(UrlDomainJson::getNumberOfTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, domainJson) -> map.put(domainJson.getDomain(),
                                    domainJson.getNumberOfTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.DOMAIN_TWEET_COUNT, mapAttributeCount);
                    break;
                case Constants.DOMAIN_UNIQUE_TWEET_COUNT:
                    mapAttributeCount = domainJsons.parallelStream()
                            .sorted(Comparator.comparing(UrlDomainJson::getNumberOfUniqueTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, domainJson) -> map.put(domainJson.getDomain(),
                                    domainJson.getNumberOfUniqueTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.DOMAIN_UNIQUE_TWEET_COUNT, mapAttributeCount);
                    break;
                case Constants.URL_TWEET_COUNT:
                    mapAttributeCount = domainJsons.parallelStream()
                            .map(UrlDomainJson::getUrlJsons)
                            .flatMap(Set::stream)
                            .sorted(Comparator.comparing(UrlJson::getNumberOfTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, urlJson) -> map.put(urlJson.getUrl(),
                                    urlJson.getNumberOfTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.URL_TWEET_COUNT, mapAttributeCount);
                    break;
                case Constants.URL_UNIQUE_TWEET_COUNT:
                    mapAttributeCount = domainJsons.parallelStream()
                            .map(UrlDomainJson::getUrlJsons)
                            .flatMap(Set::stream)
                            .sorted(Comparator.comparing(UrlJson::getNumberOfUniqueTweets).reversed())
                            .limit(topNumber)
                            .collect(LinkedHashMap::new, (map, urlJson) -> map.put(urlJson.getUrl(),
                                    urlJson.getNumberOfUniqueTweets()), Map::putAll);
                    mapFieldAttributeCount.put(Constants.URL_UNIQUE_TWEET_COUNT, mapAttributeCount);
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        // Return the desired top fields keeping order
        return mapFieldAttributeCount;
    }

    public Map<String, SummaryStatistics> getSummaryByFields(List<UrlDomainJson> domainJsons, String... fields) {
        Map<String, SummaryStatistics> mapFieldSummary = new HashMap<>();
        DoubleSummaryStatistics summary;

        if (fields.length == 0) {
            log.warn("No fields selected, retrieving information for all possible fields...");
            // Get all possible values
            fields = Constants.URL_SUMMARY_VALUES;
        }

        for (String field : fields) {
            switch (field) {
                case Constants.DOMAIN_TWEET_SUMMARY:
                    summary = domainJsons.parallelStream()
                            .map(UrlDomainJson::getNumberOfTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DOMAIN_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.DOMAIN_UNIQUE_TWEET_SUMMARY:
                    summary = domainJsons.parallelStream()
                            .map(UrlDomainJson::getNumberOfUniqueTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.DOMAIN_UNIQUE_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.URL_TWEET_SUMMARY:
                    summary = domainJsons.parallelStream()
                            .map(UrlDomainJson::getUrlJsons)
                            .flatMap(Set::stream)
                            .map(UrlJson::getNumberOfTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.URL_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                case Constants.URL_UNIQUE_TWEET_SUMMARY:
                    summary = domainJsons.parallelStream()
                            .map(UrlDomainJson::getUrlJsons)
                            .flatMap(Set::stream)
                            .map(UrlJson::getNumberOfUniqueTweets)
                            .mapToDouble(k -> k)
                            .summaryStatistics();
                    mapFieldSummary.put(Constants.URL_UNIQUE_TWEET_SUMMARY, new SummaryStatistics(summary));
                    break;
                default:
                    log.warn("Invalid field {}", field);
                    break;
            }
        }

        return mapFieldSummary;
    }
}
