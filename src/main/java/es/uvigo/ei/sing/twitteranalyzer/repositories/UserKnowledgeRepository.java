package es.uvigo.ei.sing.twitteranalyzer.repositories;

import es.uvigo.ei.sing.twitteranalyzer.entities.UserKnowledgeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserKnowledgeRepository extends CrudRepository<UserKnowledgeEntity, Integer> {

    @Query("SELECT DISTINCT uk.country FROM UserKnowledgeEntity uk")
    Set<String> findDistinctCountries();

    @Query("SELECT DISTINCT uk.classification FROM UserKnowledgeEntity uk")
    Set<String> findDistinctClassifications();

    @Query("SELECT DISTINCT uk.role FROM UserKnowledgeEntity uk")
    Set<String> findDistinctRoles();

    @Query("SELECT DISTINCT uk.sex FROM UserKnowledgeEntity uk")
    Set<String> findDistinctSex();
}
