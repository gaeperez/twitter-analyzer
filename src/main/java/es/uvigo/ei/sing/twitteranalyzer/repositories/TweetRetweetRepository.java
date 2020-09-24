package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetRetweetEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRetweetRepository extends CrudRepository<TweetRetweetEntity, Integer> {
}
