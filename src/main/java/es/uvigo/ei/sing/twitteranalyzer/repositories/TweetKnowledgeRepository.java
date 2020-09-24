package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetKnowledgeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetKnowledgeRepository extends CrudRepository<TweetKnowledgeEntity, Integer> {
}
