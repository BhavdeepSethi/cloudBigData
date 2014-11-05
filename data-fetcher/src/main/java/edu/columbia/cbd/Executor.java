package edu.columbia.cbd;

import edu.columbia.cbd.service.TweetFetcherService;

import java.io.IOException;

/**
 * Created by bhavdeepsethi on 10/30/14.
 */
public class Executor {



    public static void main(String[] args) {
        System.out.println("Starting now!");
        TweetFetcherService tweetFetcherService = new TweetFetcherService();
        tweetFetcherService.fetchTweets();
    }

}
