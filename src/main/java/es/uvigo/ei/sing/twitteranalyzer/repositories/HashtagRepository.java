package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.HashtagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends CrudRepository<HashtagEntity, Integer> {
    Optional<HashtagEntity> findByTag(String tag);
}
