package edu.columbia.cbd;

import edu.columbia.cbd.service.TweetFetcherService;
import edu.columbia.cbd.service.impl.TweetFetcherServiceImpl;

/**
 * Created by bhavdeepsethi on 10/30/14.
 */
public class DataFetchExecutor {



    public static void main(String[] args) {
        System.out.println("Starting Tweet Fetcher now!");
        BootStrap bootStrap = BootStrap.getInstance();
        bootStrap.startUp();

        TweetFetcherService tweetFetcherService = new TweetFetcherServiceImpl();
        tweetFetcherService.fetchTweets();
    }

}
