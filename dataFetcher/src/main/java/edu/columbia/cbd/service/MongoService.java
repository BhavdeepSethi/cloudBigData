package edu.columbia.cbd.service;


import edu.columbia.cbd.dao.TweetDao;
import edu.columbia.cbd.dao.impl.TweetDaoImpl;
import edu.columbia.cbd.models.Tweet;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MongoService {

    private TweetDao tweetDao;
    private static String MONGO_HOST = "";
    private static int MONGO_PORT = 27017;

    static{
        Properties configs = new Properties();
        try {
            configs.load(MongoService.class.getClassLoader().getResourceAsStream("configs.properties"));
            MONGO_HOST = configs.getProperty("mongo.host");
            MONGO_PORT = Integer.parseInt(configs.getProperty("mongo.port"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MongoService(){
        tweetDao = new TweetDaoImpl(MONGO_HOST, MONGO_PORT);

    }

    public List<Tweet> fetchTweets(String trackName){
        return tweetDao.fetchTweets(trackName);
    }

    public void addTweet(Tweet tweet){
        tweetDao.addTweet(tweet);
    }
   
}
