package es.uvigo.ei.sing.twitteranalyzer.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_cache", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "cache_date"}))
public class UserCacheEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "cache_date", nullable = false)
    private LocalDateTime cacheDate;
    @Basic
    @Column(name = "serialized_user", nullable = false)
    private byte[] serializedUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
