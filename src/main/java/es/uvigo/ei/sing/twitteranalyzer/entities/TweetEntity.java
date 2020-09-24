package es.uvigo.ei.sing.twitteranalyzer.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "tweet")
public class TweetEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Basic
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    @Basic
    @Column(name = "annotated_text", columnDefinition = "TEXT")
    private String annotatedText;
    @Basic
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Basic
    @Column(name = "geo_lat", precision = 10, scale = 5)
    private BigDecimal geoLat;
    @Basic
    @Column(name = "geo_long", precision = 10, scale = 5)
    private BigDecimal geoLong;
    @Basic
    @Column(name = "retweeted", nullable = false)
    private Boolean retweeted;
    @Basic
    @Column(name = "retweet_count", nullable = false)
    private Integer retweetCount;
    @Basic
    @Column(name = "favourite", nullable = false)
    private Boolean favourite;
    @Basic
    @Column(name = "favourite_count", nullable = false)
    private Integer favouriteCount;
    @Basic
    @Column(name = "lang", nullable = false, length = 200)
    private String lang;
    @Basic
    @Column(name = "country", length = 200)
    private String country;
    @Basic
    @Column(name = "country_code", length = 3)
    private String countryCode;
    @Basic
    @Column(name = "possibly_sensitive", nullable = false)
    private Boolean possiblySensitive;
    @Basic
    @Column(name = "modified")
    private LocalDateTime modified;
    @Basic
    @Column(name = "length")
    private Integer length;
    @Basic
    @Column(name = "device")
    private String device;
    @Basic
    @Column(name = "in_reply_to_status_id")
    private Long inReplyToStatusId;
    @Basic
    @Column(name = "in_reply_to_screen_name", length = 20)
    private String inReplyToScreenName;
    @Basic
    @Column(name = "contributors", columnDefinition = "TEXT")
    private String contributors;
    @Basic
    @Column(name = "source", columnDefinition = "TEXT")
    private String source;
    @Basic
    @Column(name = "has_contributors", nullable = false)
    private Boolean hasContributors;
    @Basic
    @Column(name = "has_media", nullable = false)
    private String hasMedia;
    @Basic
    @Column(name = "in_reply_to_user_id")
    private Long inReplyToUserId;
    @Basic
    @Column(name = "withheld_in_countries", columnDefinition = "TEXT")
    private String withHeldInCountries;
    @Basic
    @Column(name = "parsed", nullable = false)
    private Boolean parsed;
    @Basic
    @Column(name = "mentions_count")
    private Integer mentionsCount;
    @Basic
    @Column(name = "current_user_retweet_id")
    private Long currentUserRetweetId;
    @Basic
    @Column(name = "truncated", nullable = false)
    private Boolean truncated;
    @Basic
    @Column(name = "access_level", nullable = false)
    private Integer accessLevel;
    @Basic
    @Column(name = "hastag_count", nullable = false)
    private Integer hashtagCount;
    @Basic
    @Column(name = "media_count", nullable = false)
    private Integer mediaCount;
    @Basic
    @Column(name = "url_count", nullable = false)
    private Integer urlCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tweet")
    private Set<TweetMentionEntity> tweetMentionEntities = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tweet")
    private Set<TweetRetweetEntity> tweetRetweetEntities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_hashtag", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "hashtag_id", nullable = false, updatable = false)})
    private Set<HashtagEntity> hashtags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_url", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)})
    private Set<UrlEntity> urlEntities = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tweet")
    @Transient
    private TweetCacheEntity tweetCacheEntity = null;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tweet", cascade = CascadeType.ALL)
    private TweetKnowledgeEntity tweetKnowledgeEntity = null;

    // Two tweets are equals if they have the same ID
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TweetEntity))
            return false;
        if (obj == this)
            return true;
        return this.getId().equals(((TweetEntity) obj).getId());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
