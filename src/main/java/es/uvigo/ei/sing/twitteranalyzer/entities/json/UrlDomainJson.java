package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UrlDomainJson {
    // Custom ID
    private Integer id;
    // E.g. Instagram
    private String domain;
    // Number of tweets where this Domain appears (can appear more than one time in a tweet)
    private Integer numberOfTweets = 0;
    // Number of unique tweets where this Domain appears
    private Integer numberOfUniqueTweets = 0;
    // List of tweets containing this domain (database ids)
    private Set<Long> tweetIds = new HashSet<>();
    // List of URLs referring to this domain
    private Set<UrlJson> urlJsons = new HashSet<>();

    public void incrementNumberOfTweets() {
        this.numberOfTweets += 1;
    }

    public void incrementNumberOfUniqueTweets(Long tweetId) {
        if (!tweetIds.contains(tweetId))
            this.numberOfUniqueTweets += 1;
    }

    public void addUrlIntoDomain(UrlJson urlJson) {
        // Add the URL if not exists
        if (urlJsons.stream().noneMatch(url -> url.getId().equals(urlJson.getId())))
            this.urlJsons.add(urlJson);
    }
}
