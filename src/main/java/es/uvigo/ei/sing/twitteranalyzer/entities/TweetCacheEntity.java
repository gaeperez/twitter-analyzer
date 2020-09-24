package es.uvigo.ei.sing.twitteranalyzer.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tweet_cache", uniqueConstraints = @UniqueConstraint(columnNames = {"tweet_id", "cache_date"}))
public class TweetCacheEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "cache_date", nullable = false, length = 19)
    private LocalDateTime cacheDate;
    @Basic
    @Column(name = "serialized_tweet", nullable = false)
    private byte[] serializedTweet;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private TweetEntity tweet;
}
