package edu.columbia.cbd;

import edu.columbia.cbd.dao.impl.StatusListenerImpl;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SQSService;

/**
 * Created by bhavdeepsethi on 11/26/14.
 */
public class BootStrap {

    private static BootStrap instance = null;

    public synchronized static BootStrap getInstance() {
        if(instance == null) {
            instance = new BootStrap();
        }
        return instance;
    }

    private BootStrap() {
    }

    public void startUp(){
        SQSService sqsService = new SQSService();
        Constants.TWITTER_QUEUE_URL =  sqsService.createQueue(Constants.TWITTER_QUEUE_NAME);
    }



}
