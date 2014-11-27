package edu.columbia.cbd.models;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public class Tweet {

    public Tweet(long tweetId){
        this.tweetId = tweetId;
    }

    private String id;
    private long tweetId;
    private String trackName;
    private double longitude;
    private double latitude;
    private String tweet;
    private Sentiment sentiment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

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
