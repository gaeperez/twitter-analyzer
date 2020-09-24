package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.TweetCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetCacheService {

    private final TweetCacheRepository tweetCacheRepository;

    @Autowired
    public TweetCacheService(TweetCacheRepository tweetCacheRepository) {
        this.tweetCacheRepository = tweetCacheRepository;
    }
}
