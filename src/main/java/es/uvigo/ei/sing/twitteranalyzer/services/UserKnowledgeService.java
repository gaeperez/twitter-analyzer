package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.UserKnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserKnowledgeService {

    private final UserKnowledgeRepository userKnowledgeRepository;

    @Autowired
    public UserKnowledgeService(UserKnowledgeRepository userKnowledgeRepository) {
        this.userKnowledgeRepository = userKnowledgeRepository;
    }

    public Set<String> findDistinctCountries() {
        return userKnowledgeRepository.findDistinctCountries();
    }

    public Set<String> findDistinctClassifications() {
        return userKnowledgeRepository.findDistinctClassifications();
    }

    public Set<String> findDistinctRoles() {
        return userKnowledgeRepository.findDistinctRoles();
    }

    public Set<String> findDistinctSex() {
        return userKnowledgeRepository.findDistinctSex();
    }
}
