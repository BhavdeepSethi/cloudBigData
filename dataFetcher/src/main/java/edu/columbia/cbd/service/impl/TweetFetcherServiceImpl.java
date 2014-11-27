package edu.columbia.cbd.service.impl;

import edu.columbia.cbd.dao.StreamDao;
import edu.columbia.cbd.dao.impl.StatusListenerImpl;
import edu.columbia.cbd.dao.impl.StreamDaoImpl;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.TweetFetcherService;
import twitter4j.FilterQuery;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;


public final class TweetFetcherServiceImpl implements TweetFetcherService {

    private StreamDao streamDao;


    static {
        Properties configs = new Properties();
        try {
            configs.load(TweetFetcherServiceImpl.class.getClassLoader().getResourceAsStream("configs.properties"));
            Constants.KEYWORDS = (configs.getProperty("tweet.keywords")).split(Pattern.quote(","));
        } catch (IOException e) {
            Constants.KEYWORDS = new String[]{ "apple","samsung", "manutd", "love", "facebook", "friends" };
        }

    }

    public TweetFetcherServiceImpl(){
        streamDao = new StreamDaoImpl();

    }


    public void fetchTweets(){
        StatusListenerImpl statusListener = new StatusListenerImpl();
        this.streamDao.addStatusListener(statusListener);
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.track(Constants.KEYWORDS);
        this.streamDao.filter(filterQuery);
    }

    public void close(){
        streamDao.close();
    }

}