package edu.columbia.cbd;

import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SNSService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SNSServiceImpl;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

public class WorkerBootStrap {
	private static WorkerBootStrap instance = null;

    public synchronized static WorkerBootStrap getInstance() {
        if(instance == null) {
            instance = new WorkerBootStrap();
        }
        return instance;
    }

    private WorkerBootStrap() {
    }

    public void startUp(){
        SQSService sqsService = new SQSServiceImpl();
        Constants.TWITTER_QUEUE_URL =  sqsService.createQueue(Constants.TWITTER_QUEUE_NAME);
        SNSService snsService = new SNSServiceImpl();
        Constants.TWITTER_TOPIC_ARN = snsService.createTopic(Constants.TWITTER_TOPIC_NAME);
    }

}
