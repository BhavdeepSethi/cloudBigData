package edu.columbia.cbd.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.models.Sentiment;
import edu.columbia.cbd.models.Tweet;
import edu.columbia.cbd.service.MongoService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.TweetFetcherService;
import edu.columbia.cbd.service.impl.MongoServiceImpl;
import edu.columbia.cbd.service.impl.SQSServiceImpl;
import edu.columbia.cbd.service.impl.TweetFetcherServiceImpl;
import twitter4j.*;

import java.util.Random;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */

public class StatusListenerImpl implements StatusListener {

    private MongoService mongoService;
    private SQSService sqsService;
    private Gson gson;

    public StatusListenerImpl() {
        this.mongoService = new MongoServiceImpl();
        this.sqsService = new SQSServiceImpl();
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    @Override
    public void onStatus(Status status) {
        GeoLocation geoLocation = status.getGeoLocation();
        Random random = new Random();

        long tweetId = status.getId();
        if(null != geoLocation){
            double latitude = geoLocation.getLatitude();
            double longitude = geoLocation.getLongitude();

            for (String keyword : Constants.KEYWORDS){
                if (status.getText().toLowerCase().contains(keyword)){
                    Tweet tweet = new Tweet(tweetId);
                    tweet.setLatitude(latitude);
                    tweet.setLongitude(longitude);
                    tweet.setTrackName(keyword);
                    tweet.setTweet(status.getText());

                    /*
                    if(random.nextInt()%2==0) {
                        tweet.setSentiment(new Sentiment(Sentiment.SentimentLabel.POSITIVE, -1));
                    }else {
                        tweet.setSentiment(new Sentiment(Sentiment.SentimentLabel.NEGATIVE, 1));
                    }
                    */
                    String id = mongoService.addTweet(tweet);
                    tweet.setId(id);

                    String tweetJSON = gson.toJson(tweet);
                    System.out.println(geoLocation.getLatitude()+":"+geoLocation.getLongitude()+":"+keyword);
                    System.out.println();
                    sqsService.sendMessage(Constants.TWITTER_QUEUE_URL, tweetJSON);
                }
            }
        }else{
             // For now, do nothing
            //User user = status.getUser();
            //System.out.println("User:"+user.getLocation());
            //System.out.println("User:"+user.getTimeZone());
        }

        //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //Do nothing for now
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        // Do nothing
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        // Do nothing
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Stall warning received. App may get disconnected :" + warning);
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
}

