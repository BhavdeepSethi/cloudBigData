package edu.columbia.cbd.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.models.Tweet;
import edu.columbia.cbd.service.MongoService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.TweetFetcherService;
import twitter4j.*;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */

public class StatusListenerImpl implements StatusListener {

    private MongoService mongoService;
    private SQSService sqsService;
    private Gson gson;

    public StatusListenerImpl() {
        this.mongoService = new MongoService();
        this.sqsService = new SQSService();
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    @Override
    public void onStatus(Status status) {
        GeoLocation geoLocation = status.getGeoLocation();
        long tweetId = status.getId();
        if(null != geoLocation){
            double latitude = geoLocation.getLatitude();
            double longitude = geoLocation.getLongitude();

            for (String keyword : TweetFetcherService.KEYWORDS){
                if (status.getText().toLowerCase().contains(keyword)){
                    Tweet tweet = new Tweet(tweetId);
                    tweet.setLatitude(latitude);
                    tweet.setLongitude(longitude);
                    tweet.setTrackName(keyword);
                    tweet.setTweet(status.getText());
                    String tweetJSON = gson.toJson(tweet);
                    System.out.println(geoLocation.getLatitude()+":"+geoLocation.getLongitude()+":"+keyword);
                    mongoService.addTweet(tweet);
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

