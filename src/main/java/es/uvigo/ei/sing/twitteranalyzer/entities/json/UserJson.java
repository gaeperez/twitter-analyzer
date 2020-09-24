package es.uvigo.ei.sing.twitteranalyzer.entities.json;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserJson {
    private Long id;
    private String screenName = "";
    private String name = "";
    private String location = "";
    private String description = "";
    private LocalDateTime created;
    private Integer followersCount = 0;
    private Integer friendsCount = 0;
    private Integer statusesCount = 0;
    private Boolean verified = false;
    private Boolean suspended = false;
    private String urlEntityUser = "";
    private Boolean contributor = false;
    private Boolean translator = false;
    private Integer favouritesCount = 0;
    private Integer listedCount = 0;
    private String userKnowledgeEntitySex = "";
    private String userKnowledgeEntityClassification = "";
    private String userKnowledgeEntityRole = "";
    private String userKnowledgeEntityCity = "";
    private String userKnowledgeEntityCountry = "";
    private Boolean userKnowledgeEntityIsInfluence = false;
    // Custom variables
    // # Followers / # Followings (>1.5 celeb, 0.5 > normal < 1.5 , > 0.5 spam)
    private Double ffRatio = 0.0;
    // Total RT in topic / Total tweet in topic (>80 popular, 2.5 - 80 active, 0.5 - 2.5 normal, > 0.5 inactive)
    private Double rtRatio = 0.0;
    // Total favourites in topic / Total tweet in topic
    private Double favRatio = 0.0;
    // Number of favourites in the general topic (e.x. universe of wines)
    private Integer favouritesCountInTopic = 0;
    // Number of RTs in the general topic (e.x. universe of wines)
    private Integer rtsCountInTopic = 0;
    // Number of messages in the general topic (e.x. universe of wines)
    private Integer statusesCountInTopic = 0;
}
