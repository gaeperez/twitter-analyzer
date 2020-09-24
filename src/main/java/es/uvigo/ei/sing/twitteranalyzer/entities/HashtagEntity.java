package es.uvigo.ei.sing.twitteranalyzer.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "hashtag")
public class HashtagEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "tag", unique = true, nullable = false, length = 300)
    private String tag;

    private transient Long totalTweets;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "tweet_hashtag", joinColumns = {
            @JoinColumn(name = "hashtag_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    @Expose(serialize = false, deserialize = false)
    private Set<TweetEntity> tweetEntities = new HashSet<>();
}
