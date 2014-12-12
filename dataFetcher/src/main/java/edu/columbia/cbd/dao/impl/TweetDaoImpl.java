package edu.columbia.cbd.dao.impl;

import com.mongodb.*;
import edu.columbia.cbd.dao.TweetDao;
import edu.columbia.cbd.models.Tweet;
import org.bson.types.ObjectId;

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
    public String addTweet(Tweet tweet) {
        DB db = mongoClient.getDB(DATABASE_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);
        BasicDBObject document = new BasicDBObject();

        document.put("tweetId", tweet.getTweetId());
        document.put("latitude", tweet.getLatitude());
        document.put("longitude", tweet.getLongitude());
        document.put("trackName", tweet.getTrackName());
        document.put("tweet", tweet.getTweet());
        if(tweet.getSentiment()!=null) {
            document.put("sentiment", tweet.getSentiment().getSentimentLabel().name());
            document.put("score", tweet.getSentiment().getScore());
        }

        collection.insert(document);
        ObjectId id = (ObjectId) document.get( "_id" );
        return id.toString();
    }

    @Override
    public void updateTweet(Tweet tweet){
        DB db = mongoClient.getDB(DATABASE_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);
        BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(tweet.getId()));
        BasicDBObject updateValues = new BasicDBObject();
        updateValues.put("sentiment", tweet.getSentiment().getSentimentLabel().name());
        updateValues.put("score", tweet.getSentiment().getScore());
        BasicDBObject newDocument = new BasicDBObject("$set", updateValues);
        collection.update(searchQuery, newDocument);
    }
}
