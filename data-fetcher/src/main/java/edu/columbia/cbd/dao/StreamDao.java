package edu.columbia.cbd.dao;

import twitter4j.FilterQuery;
import twitter4j.StatusListener;

/**
 * Created by bhavdeepsethi on 10/29/14.
 */
public interface StreamDao {

    // Can add more functionality
    void addStatusListener(StatusListener statusListener);

    void filter(FilterQuery query);

    void close();
}
