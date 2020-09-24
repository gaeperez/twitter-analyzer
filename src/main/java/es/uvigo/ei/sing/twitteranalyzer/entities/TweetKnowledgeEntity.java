package es.uvigo.ei.sing.twitteranalyzer.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "tweet_knowledge")
public class TweetKnowledgeEntity implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "polarity", length = 45)
    private String polarity;
    @Basic
    @Column(name = "subjectivity", length = 45)
    private String subjectivity;
    @Basic
    @Column(name = "curated", nullable = false)
    private Boolean curated;
    @Basic
    @Column(name = "parsed", nullable = false)
    private Boolean parsed;
    @Basic
    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens = 0;
    @Basic
    @Column(name = "self_repeated_count", nullable = false)
    private Integer selfRepeatedCount = 0;
    @Basic
    @Column(name = "other_repeated_count", nullable = false)
    private Integer otherRepeatedCount = 0;
    @Basic
    @Column(name = "source_chain", nullable = false)
    private Boolean isSourceChain;
    @Basic
    @Column(name = "source_tweet_id", nullable = false)
    private long sourceTweetId;
    @Basic
    @Column(name = "thematic", length = 45)
    private String thematic;
    @Basic
    @Column(name = "checked_duplicity", nullable = false)
    private Boolean checkedDuplicity;
    @Basic
    @Column(name = "short_message", nullable = false)
    private Boolean shortMessage;
    @Basic
    @Column(name = "sentiment_compound")
    private Float sentimentCompound;
    @Basic
    @Column(name = "sentiment")
    private Boolean sentiment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private TweetEntity tweet;
}