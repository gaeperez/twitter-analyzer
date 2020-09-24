package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.entities.HashtagEntity;
import es.uvigo.ei.sing.twitteranalyzer.repositories.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Autowired
    public HashtagService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    public Optional<HashtagEntity> findByTag(String tag) {
        return hashtagRepository.findByTag(tag);
    }
}
