package edu.columbia.cbd.dao.impl;

import edu.columbia.cbd.dao.StreamDao;
import twitter4j.FilterQuery;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public class StreamDaoImpl implements StreamDao {

    private TwitterStream twitterStream;
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public StreamDaoImpl(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        Properties configs = new Properties();
        try {
            configs.load(StreamDaoImpl.class.getClassLoader().getResourceAsStream("configs.properties"));
            consumerKey = configs.getProperty("twitter.consumerKey");
            consumerSecret = configs.getProperty("twitter.consumerSecret");
            accessToken = configs.getProperty("twitter.accessToken");
            accessTokenSecret = configs.getProperty("twitter.accessTokenSecret");

        } catch (IOException e) {
            e.printStackTrace();
        }
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

    }


    public void addStatusListener(StatusListener statusListener){
        twitterStream.addListener(statusListener);
    }

    public void filter(FilterQuery query){
        twitterStream.filter(query);
    }

    public void close(){
        twitterStream.clearListeners();
        twitterStream.cleanUp();
    }

}
