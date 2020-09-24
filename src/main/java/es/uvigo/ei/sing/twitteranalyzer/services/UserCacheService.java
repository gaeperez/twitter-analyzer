package es.uvigo.ei.sing.twitteranalyzer.services;

import es.uvigo.ei.sing.twitteranalyzer.repositories.UserCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;

    @Autowired
    public UserCacheService(UserCacheRepository userCacheRepository) {
        this.userCacheRepository = userCacheRepository;
    }
}
