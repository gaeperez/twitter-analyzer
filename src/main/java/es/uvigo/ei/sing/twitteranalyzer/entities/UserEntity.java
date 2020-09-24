package es.uvigo.ei.sing.twitteranalyzer.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "user")
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Basic
    @Column(name = "screen_name", nullable = false, length = 22)
    private String screenName;
    @Basic
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;
    @Basic
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    @Expose(serialize = false, deserialize = false)
    private String profileImageUrl;
    @Basic
    @Column(name = "location", length = 200)
    private String location;
    @Basic
    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
    @Basic
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Basic
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Basic
    @Column(name = "followers_count")
    private Integer followersCount;
    @Basic
    @Column(name = "friends_count")
    private Integer friendsCount;
    @Basic
    @Column(name = "statuses_count")
    private Integer statusesCount;
    @Basic
    @Column(name = "time_zone", length = 50)
    private String timeZone;
    @Basic
    @Column(name = "last_update", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private LocalDateTime lastUpdate;
    @Basic
    @Column(name = "lang", length = 5)
    @Expose(serialize = false, deserialize = false)
    private String lang;
    @Basic
    @Column(name = "utc_ofset")
    private Integer utcOffset;
    @Basic
    @Column(name = "verified", nullable = false)
    private Boolean verified;
    @Basic
    @Column(name = "inserted", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private LocalDateTime inserted;
    @Basic
    @Column(name = "status_geo_lat", precision = 10, scale = 5)
    @Expose(serialize = false, deserialize = false)
    private BigDecimal statusGeoLat;
    @Basic
    @Column(name = "status_geo_long", precision = 10, scale = 5)
    @Expose(serialize = false, deserialize = false)
    private BigDecimal statusGeoLong;
    @Basic
    @Column(name = "status_country", length = 200)
    @Expose(serialize = false, deserialize = false)
    private String statusCountry;
    @Basic
    @Column(name = "status_country_code", length = 5)
    @Expose(serialize = false, deserialize = false)
    private String statusCountryCode;
    @Basic
    @Column(name = "suspended", nullable = false)
    private Boolean suspended;
    @Basic
    @Column(name = "url_entity", columnDefinition = "TEXT")
    private String urlEntityUser;
    @Basic
    @Column(name = "contributor", nullable = false)
    private Boolean contributor;
    @Basic
    @Column(name = "translator", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean translator;
    @Basic
    @Column(name = "default_profile", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean defaultProfile;
    @Basic
    @Column(name = "default_profile_image", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean defaultProfileImage;
    @Basic
    @Column(name = "show_all_inline_media", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean showAllInlineMedia;
    @Basic
    @Column(name = "geo_enabled", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean geoEnabled;
    @Basic
    @Column(name = "protected_user", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean protectedUser;
    @Basic
    @Column(name = "email", length = 254)
    @Expose(serialize = false, deserialize = false)
    private String email;
    @Basic
    @Column(name = "favourites_count", nullable = false)
    private Integer favouritesCount;
    @Basic
    @Column(name = "access_level", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Integer accessLevel;
    @Basic
    @Column(name = "listed_count", nullable = false)
    private Integer listedCount;
    @Basic
    @Column(name = "parsed", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean parsed;

    private transient Long totalTweets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Expose(serialize = false, deserialize = false)
    private Set<TweetEntity> tweets = new HashSet<>(0);

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private UserKnowledgeEntity userKnowledgeEntity = null;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Expose(serialize = false, deserialize = false)
    private UserCacheEntity userCacheEntity = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userBySourceUserId", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<TweetMentionEntity> tweetMentionsForSourceUserId = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userByTargetUserId")
    private Set<TweetMentionEntity> tweetMentionsForTargetUserId = new HashSet<>(0);

    // Two UserEntities are the same when their ID (i.e. TwitterID) is the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
