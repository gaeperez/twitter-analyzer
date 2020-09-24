package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.TweetKnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetKnowledgeService {

    private final TweetKnowledgeRepository tweetKnowledgeRepository;

    @Autowired
    public TweetKnowledgeService(TweetKnowledgeRepository tweetKnowledgeRepository) {
        this.tweetKnowledgeRepository = tweetKnowledgeRepository;
    }
}
