package es.uvigo.ei.sing.twitteranalyzer;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import es.uvigo.ei.sing.twitteranalyzer.controllers.AppController;
import es.uvigo.ei.sing.twitteranalyzer.controllers.TweetController;
import es.uvigo.ei.sing.twitteranalyzer.entities.json.TweetJson;
import es.uvigo.ei.sing.twitteranalyzer.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Log4j2
@SpringBootApplication
@EnableCaching
public class TwitterAnalyzerApplication implements CommandLineRunner {

    @Autowired
    private AppController appController;

    public static void main(String[] args) {
        SpringApplication.run(TwitterAnalyzerApplication.class, args);
    }

    @Bean
    public StanfordCoreNLP pipeline() {
        // Create StanfordCoreNLP object properties, with POS tagging (required for lemmatization), and lemmatization
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        // StanfordCoreNLP loads a lot of models, so you probably only want to do this once per execution
        return new StanfordCoreNLP(props);
    }

    @Override
    public void run(String... args) throws IOException {
        String region = Constants.REGION_PORTO;
        String topic = Constants.TOPIC_ALL;
        int topNumber = 25;
        float minNegative = -0.7f;
        float minPositive = 0.7f;
        int nGram = 2;

        // Parse PORTO-ALL
        log.info("Starting the parsing of region {} with topic {}...", region, topic);
        List<TweetJson> allTweetJson = appController.retrieveTweetsByRegionAndTopic(region, topic, true);
        appController.parseUsersByRegionAndTopic(region, topic, topNumber, false, allTweetJson);
        log.info("Finishing the parsing of region {} with topic {}...", region, topic);

        // Parse DOURO-ALL
        region = Constants.REGION_DOURO;
        log.info("Starting the parsing of region {} with topic {}...", region, topic);
        allTweetJson = appController.retrieveTweetsByRegionAndTopic(region, topic, true);
        appController.parseUsersByRegionAndTopic(region, topic, topNumber, false, allTweetJson);
        log.info("Finishing the parsing of region {} with topic {}...", region, topic);

        // Parse OTHERS-ALL
        region = Constants.REGION_OTHERS;
        log.info("Starting the parsing of region {} with topic {}...", region, topic);
        allTweetJson = appController.retrieveTweetsByRegionAndTopic(region, topic, true);
        appController.parseUsersByRegionAndTopic(region, topic, topNumber, false, allTweetJson);
        log.info("Finishing the parsing of region {} with topic {}...", region, topic);
    }
}
