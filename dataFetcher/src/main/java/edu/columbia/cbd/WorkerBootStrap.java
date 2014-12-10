package edu.columbia.cbd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

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
    	SNSServiceImpl snsService = new SNSServiceImpl();
		//create a new SNS topic
		Constants.TWITTER_TOPIC_ARN= snsService.createTopic(Constants.TWITTER_TOPIC_NAME);
		
		SQSServiceImpl sqsServiceOutgoing = new SQSServiceImpl();
		Constants.TWITTER_OUTGOING_QUEUE_URL =  sqsServiceOutgoing.createQueue(Constants.TWITTER_OUTGOING_QUEUE_NAME);
		//getting queue arn
		Constants.TWITTER_OUTGOING_QUEUE_ARN = sqsServiceOutgoing.getSQSArn(Constants.TWITTER_OUTGOING_QUEUE_URL);
		
		
		//Give permission to the Amazon SNS topic to send messages to the Amazon SQS queue
		Map map= new HashMap<String,String>();
		map.put("Policy", "Sid=MySQSPolicy001");
		SetQueueAttributesRequest attributesRequest = new SetQueueAttributesRequest(createQueue,map);
		
		amazonSQSClient.setQueueAttributes(attributesRequest);
		//attributesRequest.
		
		
		
		//subscribe sqs 
		snsService.subscribeTopic(Constants.TWITTER_TOPIC_ARN, "sqs", Constants.TWITTER_OUTGOING_QUEUE_ARN);
		
		
		//Give users permissions to the appropriate topic and queue actions
		
		
		
		
		
		    
    
    }

}
