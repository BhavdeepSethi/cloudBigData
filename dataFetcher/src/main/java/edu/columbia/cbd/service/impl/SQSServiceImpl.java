package edu.columbia.cbd.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SQSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavdeepsethi on 11/26/14.
 */
public class SQSServiceImpl implements SQSService{

    AmazonSQS sqs;
    public SQSServiceImpl(){
        AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
                    SQSServiceImpl.class.getClassLoader().getResourceAsStream("AwsCredentials.properties"));

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +e);
        }
        sqs = new AmazonSQSClient(credentials);
        Region usWest2 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usWest2);
    }

    public String createQueue(String queueName){
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        return sqs.createQueue(createQueueRequest).getQueueUrl();
    }

    public List<String> listQueues(){
        List<String> queueList = new ArrayList<String>();
        for (String queueUrl : sqs.listQueues().getQueueUrls()) {
            queueList.add(queueUrl);
            System.out.println("  QueueUrl: " + queueUrl);
        }
        return queueList;
    }

    public void sendMessage(String queueUrl, String msg){
        sqs.sendMessage(new SendMessageRequest(queueUrl, msg));
    }

    public List<Message> receiveMessage(String queueUrl){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }

    public void deleteMessage(String queueUrl, String handleReceipt){
        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(queueUrl, handleReceipt);
        sqs.deleteMessage(deleteMessageRequest);
    }

    public String getSQSArn(String queueUrl){
    	GetQueueAttributesRequest queueAttributesRequest = new GetQueueAttributesRequest(queueUrl)
    	 .withAttributeNames("All");
    	GetQueueAttributesResult queueAttributesResult = sqs.getQueueAttributes(queueAttributesRequest);
    	Map<String, String> sqsAttributeMap = queueAttributesResult.getAttributes();
    	return sqsAttributeMap.get("QueueArn");
    	/*ArrayList<String> attributeNames = new ArrayList<String>();
    	attributeNames.add("All");
    	GetQueueAttributesResult queueAttributes = sqs.getQueueAttributes(new GetQueueAttributesRequest(queueUrl ,attributeNames));
		Map<String, String> attributes = queueAttributes.getAttributes();
		return attributes.get("QueueArn");	*/	
    }
    
    public void setAttribute(Policy policy,String url){
    	HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("Policy", policy.toJson());
		SetQueueAttributesRequest request = new SetQueueAttributesRequest(url, attributes);
		sqs.setQueueAttributes(request);
		
    }

}
