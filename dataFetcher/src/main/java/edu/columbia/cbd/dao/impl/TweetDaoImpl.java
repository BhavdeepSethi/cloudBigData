package edu.columbia.cbd.dao.impl;

import com.mongodb.*;
import edu.columbia.cbd.dao.TweetDao;
import edu.columbia.cbd.models.Tweet;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public class TweetDaoImpl implements TweetDao {

    private MongoClient mongoClient;
    private static String DATABASE_NAME = "cbd";
    private static String COLLECTION_NAME = "geoTweet";



    public TweetDaoImpl(String host, int port){
        try{
            this.mongoClient = new MongoClient(host , port);
        }catch (UnknownHostException e){
            throw new RuntimeException("Cannot connect to MongoDB at: "+host+":"+port);
        }
    }

    @Override
    public List<Tweet> fetchTweets(String trackName) {
        DB db = mongoClient.getDB(DATABASE_NAME);
        DBCollection table = db.getCollection(COLLECTION_NAME);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("trackName",  trackName);
        DBCursor cursor = table.find(searchQuery);
        List<Tweet> tweetList = new ArrayList<Tweet>();
        while (cursor.hasNext()) {
            DBObject dbObject =  cursor.next();
            System.out.println(dbObject);
            Tweet tweet = new Tweet((Long) dbObject.get("tweetId"));
            tweet.setLatitude((Double) dbObject.get("latitude"));
            tweet.setLongitude((Double) dbObject.get("longitude"));
            tweet.setTrackName((String) dbObject.get("trackName"));
            tweetList.add(tweet);
        }
        return tweetList;
    }

    @Override
    public void addTweet(Tweet tweet) {
        DB db = mongoClient.getDB(DATABASE_NAME);
        DBCollection table = db.getCollection(COLLECTION_NAME);
        BasicDBObject document = new BasicDBObject();

        document.put("tweetId", tweet.getTweetId());
        document.put("latitude", tweet.getLatitude());
        document.put("longitude", tweet.getLongitude());
        document.put("trackName", tweet.getTrackName());
        table.insert(document);
    }
}
