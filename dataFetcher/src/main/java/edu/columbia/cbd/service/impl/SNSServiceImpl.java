package edu.columbia.cbd.service.impl;

import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.model.Message;

import edu.columbia.cbd.service.SNSService;

public class SNSServiceImpl implements SNSService {

	AmazonSNSClient sns;
	public SNSServiceImpl(){
        AWSCredentials credentials = null;
        try {
            credentials = new PropertiesCredentials(
                    SQSServiceImpl.class.getClassLoader().getResourceAsStream("AwsCredentials.properties"));

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +e);
        }
        sns = new AmazonSNSClient(credentials);
        Region usWest2 = Region.getRegion(Regions.US_EAST_1);
        sns.setRegion(usWest2);
    }
	
	@Override
	public String createTopic(String topicName) {
		// TODO Auto-generated method stub
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
		CreateTopicResult createTopicResult = sns.createTopic(createTopicRequest);
		return createTopicResult.getTopicArn();
		
	}

	@Override
	public List<String> listTopic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(String queueUrl, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Message> receiveMessage(String topicUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMessage(String topicUrl, String handleReceipt) {
		// TODO Auto-generated method stub

	}

}
