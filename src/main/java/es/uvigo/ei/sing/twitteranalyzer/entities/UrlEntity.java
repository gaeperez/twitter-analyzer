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
@Table(name = "url", uniqueConstraints = @UniqueConstraint(columnNames = "hash"))
public class UrlEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;
    @Basic
    @Column(name = "expanded_url", nullable = false, columnDefinition = "TEXT")
    private String expandedUrl;
    @Basic
    @Column(name = "hash", unique = true, nullable = false, length = 40)
    @Expose(serialize = false, deserialize = false)
    private String hash;
    @Basic
    @Column(name = "cluster")
    @Expose(serialize = false, deserialize = false)
    private String cluster;
    @Basic
    @Column(name = "resolved", columnDefinition = "TEXT")
    @Expose(serialize = false, deserialize = false)
    private String resolved;
    @Basic
    @Column(name = "title")
    @Expose(serialize = false, deserialize = false)
    private String title;
    @Basic
    @Column(name = "html")
    @Lob
    @Expose(serialize = false, deserialize = false)
    private String html;
    @Basic
    @Column(name = "content")
    @Lob
    @Expose(serialize = false, deserialize = false)
    private String content;
    @Basic
    @Column(name = "error", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean error;
    @Basic
    @Column(name = "parsed", nullable = false)
    @Expose(serialize = false, deserialize = false)
    private Boolean parsed;
    @Basic
    @Column(name = "language", length = 45)
    @Expose(serialize = false, deserialize = false)
    private String language;

    private transient Long totalCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_url", joinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    @Expose(serialize = false, deserialize = false)
    private Set<TweetEntity> tweets = new HashSet<>();
}
