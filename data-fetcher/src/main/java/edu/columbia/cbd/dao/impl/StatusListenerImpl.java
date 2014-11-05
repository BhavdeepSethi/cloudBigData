package edu.columbia.cbd.dao.impl;

import edu.columbia.cbd.models.Tweet;
import edu.columbia.cbd.service.MongoService;
import edu.columbia.cbd.service.TweetFetcherService;
import twitter4j.*;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */

public class StatusListenerImpl implements StatusListener {

    private MongoService mongoService;

    public StatusListenerImpl() {
        this.mongoService = new MongoService();
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
                    System.out.println(geoLocation.getLatitude()+":"+geoLocation.getLongitude()+":"+keyword);
                    mongoService.addTweet(tweet);
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

