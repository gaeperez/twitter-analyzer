package es.uvigo.ei.sing.twitteranalyzer.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public final String RES = "results/";
    public final String RES_TWEETS = "_tweets.json";
    public final String RES_TWEETS_COUNT = "_tweets_count.json";
    public final String RES_TWEETS_SUMMARY = "_tweets_summary.json";
    public final String RES_TWEETS_NGRAMS = "_tweets_ngram.json";
    public final String RES_TWEETS_DATE = "_tweets_date.json";
    public final String RES_TWEETS_SENTIMENT = "_tweets_sentiment.json";
    public final String RES_TWEETS_JOINTOK = "_tweets_join-token.json";
    public final String RES_USERS = "_users.json";
    public final String RES_USERS_COUNT = "_users_count.json";
    public final String RES_USERS_SUMMARY = "_users_summary.json";
    public final String RES_HTS = "_hashtags.json";
    public final String RES_HTS_COUNT = "_hashtags_count.json";
    public final String RES_HTS_SUMMARY = "_hashtags_summary.json";
    public final String RES_MENTIONS = "_mentions.json";
    public final String RES_MENTIONS_COUNT = "_mentions_count.json";
    public final String RES_MENTIONS_SUMMARY = "_mentions_summary.json";
    public final String RES_URLS = "_urls.json";
    public final String RES_URLS_COUNT = "_urls_count.json";
    public final String RES_URLS_SUMMARY = "_urls_summary.json";

    public final String TWEET_RT_COUNT = "tweet_retweet_count";
    public final String TWEET_FAV_COUNT = "tweet_favourite_count";
    public final String TWEET_MENTION_COUNT = "tweet_mention_count";
    public final String TWEET_HASHTAG_COUNT = "tweet_hashtag_count";
    public final String TWEET_MEDIA_COUNT = "tweet_media_count";
    public final String TWEET_URL_COUNT = "tweet_url_count";
    public final String TWEET_SOURCE_COUNT = "tweet_source_count";
    public final String TWEET_REPLY_COUNT = "tweet_reply_count";
    public final String TWEET_CREATED_SUMMARY = "created_summary";
    public final String TWEET_RT_SUMMARY = "tweet_retweet_summary";
    public final String TWEET_FAV_SUMMARY = "tweet_favourite_summary";
    public final String TWEET_MENTION_SUMMARY = "tweet_mention_summary";
    public final String TWEET_HASHTAG_SUMMARY = "tweet_hashtag_summary";
    public final String TWEET_MEDIA_SUMMARY = "tweet_media_summary";
    public final String TWEET_URL_SUMMARY = "tweet_url_summary";
    public final String TWEET_SENTIMENT_SUMMARY = "sentiment_summary";
    public final String TWEET_POSITIVE = "positive";
    public final String TWEET_NEUTRAL = "neutral";
    public final String TWEET_NEGATIVE = "negative";

    public final String USER_FOLLOWERS_COUNT = "user_followers_count";
    public final String USER_FRIENDS_COUNT = "user_friends_count";
    public final String USER_STATUSES_COUNT = "user_statuses_count";
    public final String USER_STATUSES_TOPIC_COUNT = "user_statuses-topic_count";
    public final String USER_FAVOURITES_COUNT = "user_favourites_count";
    public final String USER_FAVOURITES_TOPIC_COUNT = "user_favourites-topic_count";
    public final String USER_LISTED_COUNT = "user_listed_count";
    public final String USER_FF_RATIO_COUNT = "user_ff-ratio_count";
    public final String USER_RT_RATIO_COUNT = "user_rt-ratio_count";
    public final String USER_FAV_RATIO_COUNT = "user_fav-ratio_count";
    public final String USER_SEX_COUNT = "user_sex_count";
    public final String USER_CLASSIFICATION_COUNT = "user_classification_count";
    public final String USER_ROLE_COUNT = "user_role_count";
    public final String USER_COUNTRY_COUNT = "user_country_count";
    public final String USER_FOLLOWERS_SUMMARY = "user_followers_summary";
    public final String USER_FRIENDS_SUMMARY = "user_friends_summary";
    public final String USER_STATUSES_SUMMARY = "user_statuses_summary";
    public final String USER_STATUSES_TOPIC_SUMMARY = "user_statuses-topic_summary";
    public final String USER_FAVOURITES_SUMMARY = "user_favourites_summary";
    public final String USER_FAVOURITES_TOPIC_SUMMARY = "user_favourites-topic_summary";
    public final String USER_LISTED_SUMMARY = "user_listed_summary";
    public final String USER_FF_RATIO_SUMMARY = "user_ff-ratio_summary";
    public final String USER_RT_RATIO_SUMMARY = "user_rt-ratio_summary";
    public final String USER_FAV_RATIO_SUMMARY = "user_fav-ratio_summary";

    public final String HASHTAG_TWEET_COUNT = "hashtag_tweet_count";
    public final String HASHTAG_UNIQUE_TWEET_COUNT = "hashtag_unique-tweet_count";
    public final String HASHTAG_TWEET_SUMMARY = "hashtag_tweet_summary";
    public final String HASHTAG_UNIQUE_TWEET_SUMMARY = "hashtag_unique-tweet_summary";

    public final String MENTION_TWEET_COUNT = "mention_tweet_count";
    public final String MENTION_UNIQUE_TWEET_COUNT = "mention_unique-tweet_count";
    public final String MENTION_TWEET_SUMMARY = "mention_tweet_summary";
    public final String MENTION_UNIQUE_TWEET_SUMMARY = "mention_unique-tweet_summary";

    public final String DOMAIN_TWEET_COUNT = "domain_tweet_count";
    public final String DOMAIN_UNIQUE_TWEET_COUNT = "domain_unique-tweet_count";
    public final String URL_TWEET_COUNT = "url_tweet_count";
    public final String URL_UNIQUE_TWEET_COUNT = "url_unique-tweet_count";
    public final String DOMAIN_TWEET_SUMMARY = "domain_tweet_summary";
    public final String DOMAIN_UNIQUE_TWEET_SUMMARY = "domain_unique-tweet_summary";
    public final String URL_TWEET_SUMMARY = "url_tweet_summary";
    public final String URL_UNIQUE_TWEET_SUMMARY = "url_unique-tweet_summary";

    public final String DATE_YEAR = "year";
    public final String DATE_MONTH = "month";
    public final String DATE_DAY = "day";
    public final String DATE_HOUR = "hour";

    public final String SEX_UNK = "U";
    public final String SEX_MALE = "M";
    public final String SEX_FEMALE = "F";

    public final String CLASSI_EXPERT = "EXPERT";
    public final String CLASSI_INDIV = "INDIVIDUAL";
    public final String CLASSI_ORG = "ORGANISATION";
    public final String CLASSI_SUSPENDED = "SUSPENDED";

    public final String REGION_ALL = "all";
    public final String REGION_DOURO = "douro";
    public final String REGION_PORTO = "porto";
    public final String REGION_OTHERS = "others";
    public final String TOPIC_DOC = "doc";
    public final String TOPIC_TOURISM = "tourism";
    public final String TOPIC_ALL = "all";

    public final String POS_ADJ = "JJ";
    public final String POS_ADJS = "JJ.*";
    public final String POS_NOUN = "NN";
    public final String POS_PROPER_NOUN = "NNP";
    public final String POS_NOUNS = "NN.*";
    public final String POS_ADV = "RB";
    public final String POS_ADVS = "RB.*";
    public final String POS_VERB = "VB";
    public final String POS_VERBS = "VB.*";

    // Enum used to retrieve all processed JSONs from Controllers if no field selected
    public final String[] TWEET_SUMMARY_VALUES = new String[]{
            TWEET_CREATED_SUMMARY,
            TWEET_RT_SUMMARY,
            TWEET_FAV_SUMMARY,
            TWEET_MENTION_SUMMARY,
            TWEET_HASHTAG_SUMMARY,
            TWEET_MEDIA_SUMMARY,
            TWEET_URL_SUMMARY,
            TWEET_SENTIMENT_SUMMARY
    };

    public final String[] TWEET_COUNT_VALUES = new String[]{
            TWEET_RT_COUNT,
            TWEET_FAV_COUNT,
            TWEET_MENTION_COUNT,
            TWEET_HASHTAG_COUNT,
            TWEET_MEDIA_COUNT,
            TWEET_URL_COUNT,
            TWEET_SOURCE_COUNT,
            TWEET_REPLY_COUNT
    };

    public final String[] USER_SUMMARY_VALUES = new String[]{
            USER_FOLLOWERS_SUMMARY,
            USER_FRIENDS_SUMMARY,
            USER_STATUSES_SUMMARY,
            USER_STATUSES_TOPIC_SUMMARY,
            USER_FAVOURITES_SUMMARY,
            USER_FAVOURITES_TOPIC_SUMMARY,
            USER_LISTED_SUMMARY,
            USER_FF_RATIO_SUMMARY,
            USER_RT_RATIO_SUMMARY,
            USER_FAV_RATIO_SUMMARY
    };

    public final String[] USER_COUNT_VALUES = new String[]{
            USER_FOLLOWERS_COUNT,
            USER_FRIENDS_COUNT,
            USER_STATUSES_COUNT,
            USER_STATUSES_TOPIC_COUNT,
            USER_FAVOURITES_COUNT,
            USER_FAVOURITES_TOPIC_COUNT,
            USER_LISTED_COUNT,
            USER_FF_RATIO_COUNT,
            USER_RT_RATIO_COUNT,
            USER_FAV_RATIO_COUNT,
            USER_SEX_COUNT,
            USER_CLASSIFICATION_COUNT,
            USER_ROLE_COUNT,
            USER_COUNTRY_COUNT
    };

    public final String[] HASHTAG_SUMMARY_VALUES = new String[]{
            HASHTAG_TWEET_SUMMARY,
            HASHTAG_UNIQUE_TWEET_SUMMARY
    };

    public final String[] HASHTAG_COUNT_VALUES = new String[]{
            HASHTAG_TWEET_COUNT,
            HASHTAG_UNIQUE_TWEET_COUNT
    };

    public final String[] MENTION_SUMMARY_VALUES = new String[]{
            MENTION_TWEET_SUMMARY,
            MENTION_UNIQUE_TWEET_SUMMARY
    };

    public final String[] MENTION_COUNT_VALUES = new String[]{
            MENTION_TWEET_COUNT,
            MENTION_UNIQUE_TWEET_COUNT
    };

    public final String[] URL_SUMMARY_VALUES = new String[]{
            DOMAIN_TWEET_SUMMARY,
            DOMAIN_UNIQUE_TWEET_SUMMARY,
            URL_TWEET_SUMMARY,
            URL_UNIQUE_TWEET_SUMMARY
    };

    public final String[] URL_COUNT_VALUES = new String[]{
            DOMAIN_TWEET_COUNT,
            DOMAIN_UNIQUE_TWEET_COUNT,
            URL_TWEET_COUNT,
            URL_UNIQUE_TWEET_COUNT
    };
}
