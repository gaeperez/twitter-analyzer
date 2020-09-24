package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetMentionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetMentionRepository extends CrudRepository<TweetMentionEntity, Integer> {
}
