package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TweetJson {
    private Long id;
    private Long userId;
    private String userScreenName;
    private LocalDateTime created;
    private String text = "";
    private ProcessedTextJson processedTextJson;
    private String lang = "";
    private String source = "";
    private Integer retweetCount = 0;
    private Integer favouriteCount = 0;
    private Integer mentionsCount = 0;
    private Integer hashtagCount = 0;
    private Integer mediaCount = 0;
    private Integer urlCount = 0;
    // Custom variables
    private List<String> extractedUrls = new ArrayList<>();
    private List<String> extractedHashtags = new ArrayList<>();
    private List<String> extractedMentions = new ArrayList<>();
    private String replyName = "";
    private Float sentiment = 0.0f;
}
