package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
