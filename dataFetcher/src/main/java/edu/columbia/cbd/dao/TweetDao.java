package edu.columbia.cbd.dao;

import edu.columbia.cbd.models.Tweet;

import java.util.List;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public interface TweetDao {

    List<Tweet> fetchTweets(String trackName);

    String addTweet(Tweet tweet);

    void updateTweet(Tweet tweet);



}
