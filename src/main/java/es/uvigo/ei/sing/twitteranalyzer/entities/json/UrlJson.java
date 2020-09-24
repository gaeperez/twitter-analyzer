package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UrlJson {
    // Custom ID
    private Integer id;
    private String url;
    // Number of tweets where this URL appears (can appear more than one time in a tweet)
    private Integer numberOfTweets = 0;
    // Number of unique tweets where this URL appears
    private Integer numberOfUniqueTweets = 0;
    // List of tweets containing this URL (database ids)
    private Set<Long> tweetIds = new HashSet<>();

    public void incrementNumberOfTweets() {
        this.numberOfTweets += 1;
    }

    public void incrementNumberOfUniqueTweets(Long tweetId) {
        if (!tweetIds.contains(tweetId))
            this.numberOfUniqueTweets += 1;
    }
}
