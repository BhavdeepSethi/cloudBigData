package edu.columbia.cbd.models;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public class Tweet {

    public Tweet(long tweetId){
        this.tweetId = tweetId;
    }

    private long tweetId;
    private String trackName;
    private double longitude;
    private double latitude;

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
