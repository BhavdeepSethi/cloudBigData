package edu.columbia.cbd.service;

import edu.columbia.cbd.models.Tweet;

import java.util.List;

/**
 * Created by bhavdeepsethi on 11/26/14.
 */
public interface MongoService {

    List<Tweet> fetchTweets(String trackName);

    void addTweet(Tweet tweet);
}
