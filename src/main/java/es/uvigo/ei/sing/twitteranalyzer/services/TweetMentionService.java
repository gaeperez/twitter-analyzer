package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.TweetMentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetMentionService {

    private final TweetMentionRepository tweetMentionRepository;

    @Autowired
    public TweetMentionService(TweetMentionRepository tweetMentionRepository) {
        this.tweetMentionRepository = tweetMentionRepository;
    }
}
