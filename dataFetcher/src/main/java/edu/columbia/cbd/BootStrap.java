package edu.columbia.cbd;

import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SNSService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SNSServiceImpl;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

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
        SQSService sqsService = new SQSServiceImpl();
        Constants.TWITTER_QUEUE_URL =  sqsService.createQueue(Constants.TWITTER_QUEUE_NAME);
        SNSService snsService = new SNSServiceImpl();
        Constants.TWITTER_TOPIC_ARN = snsService.createTopic(Constants.TWITTER_TOPIC_NAME);
    }



}
