package es.uvigo.ei.sing.twitteranalyzer.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "user_knowledge",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id", "user_id"}),
        indexes = {
                @Index(name = "role", columnList = "role"),
                @Index(name = "country", columnList = "country"),
                @Index(name = "classification", columnList = "classification"),
                @Index(name = "sex", columnList = "sex")
        })
public class UserKnowledgeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "sex", length = 1)
    private String sex;
    @Basic
    @Column(name = "years")
    private Integer years;
    @Basic
    @Column(name = "classification", length = 254)
    private String classification;
    @Basic
    @Column(name = "role", length = 254)
    private String role;
    @Basic
    @Column(name = "city", length = 254)
    private String city;
    @Basic
    @Column(name = "country_code", length = 45)
    private String countryCode;
    @Basic
    @Column(name = "country", length = 254)
    private String country;
    @Basic
    @Column(name = "continent", length = 254)
    private String continent;
    @Basic
    @Column(name = "is_influencer")
    private Boolean isInfluencer;
    @Basic
    @Column(name = "image_blob")
    private byte[] imageBlob;
    @Basic
    @Column(name = "curated", nullable = false)
    private Boolean curated;
    @Basic
    @Column(name = "parsed", nullable = false)
    private Boolean parsed;
    @Basic
    @Column(name = "geo_lat", precision = 10, scale = 5)
    private BigDecimal geoLat;
    @Basic
    @Column(name = "geo_long", precision = 10, scale = 5)
    private BigDecimal geoLong;
    @Basic
    @Column(name = "detected_gender_by", length = 254)
    private String detectedGenderBy;
    @Basic
    @Column(name = "influencer_measure")
    private Double influencerMeasure = 0.0d;
    //https://workmacro.com/instagram/follower-following-ratio-say-instagram-account/
    @Basic
    @Column(name = "ftf_ratio")
    private Double ftfRatio = 0.0d;
    @Basic
    @Column(name = "cluster", length = 45)
    private String cluster;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
