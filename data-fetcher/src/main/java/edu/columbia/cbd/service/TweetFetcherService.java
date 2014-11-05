package edu.columbia.cbd.service;

import edu.columbia.cbd.dao.StreamDao;
import edu.columbia.cbd.dao.impl.StatusListenerImpl;
import edu.columbia.cbd.dao.impl.StreamDaoImpl;
import twitter4j.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;


public final class TweetFetcherService {

    private StreamDao streamDao;

    public static String[] KEYWORDS;

    static {
        Properties configs = new Properties();
        try {
            configs.load(TweetFetcherService.class.getClassLoader().getResourceAsStream("configs.properties"));
            KEYWORDS = (configs.getProperty("tweet.keywords")).split(Pattern.quote(","));
        } catch (IOException e) {
            KEYWORDS = new String[]{ "apple","samsung", "manutd", "love", "facebook", "friends" };
        }

    }

    public TweetFetcherService(){
        streamDao = new StreamDaoImpl();

    }


    public void fetchTweets(){
        StatusListenerImpl statusListener = new StatusListenerImpl();
        this.streamDao.addStatusListener(statusListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(KEYWORDS);
        this.streamDao.filter(filterQuery);
    }

    public void close(){
        streamDao.close();
    }

}