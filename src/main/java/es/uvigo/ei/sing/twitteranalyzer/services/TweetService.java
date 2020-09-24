package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.entities.TweetEntity;
import es.uvigo.ei.sing.twitteranalyzer.repositories.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;

    @Autowired
    public TweetService(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public Set<TweetEntity> findAll() {
        return tweetRepository.findAll();
    }

    public Set<TweetEntity> findAllEnglishPorto() {
        return tweetRepository.findAllEnglishPorto();
    }

    public Set<TweetEntity> findAllEnglishDouro() {
        return tweetRepository.findAllEnglishDouro();
    }

    public Set<TweetEntity> findAllEnglishOthers() {
        return tweetRepository.findAllEnglishOthers();
    }

    public Set<TweetEntity> findAllPortoDoc() {
        return tweetRepository.findAllPortoDoc();
    }

    public Set<TweetEntity> findAllDouroDoc() {
        return tweetRepository.findAllDouroDoc();
    }

    public Set<TweetEntity> findAllOthersDoc() {
        return tweetRepository.findAllOthersDoc();
    }

    public Set<TweetEntity> findAllPortoTourism() {
        return tweetRepository.findAllPortoTourism();
    }

    public Set<TweetEntity> findAllDouroTourism() {
        return tweetRepository.findAllDouroTourism();
    }

    public Set<TweetEntity> findAllOthersTourism() {
        return tweetRepository.findAllOthersTourism();
    }

}
