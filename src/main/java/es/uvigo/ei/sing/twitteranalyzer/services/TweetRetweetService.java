package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.TweetRetweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetRetweetService {

    private final TweetRetweetRepository tweetRetweetRepository;

    @Autowired
    public TweetRetweetService(TweetRetweetRepository tweetRetweetRepository) {
        this.tweetRetweetRepository = tweetRetweetRepository;
    }
}
