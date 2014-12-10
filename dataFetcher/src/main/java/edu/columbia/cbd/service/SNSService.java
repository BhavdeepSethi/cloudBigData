package edu.columbia.cbd.service;

import java.util.List;

import com.amazonaws.services.sqs.model.Message;

public interface SNSService {
	
	String createTopic(String topicName);

    List<String> listTopic();

    void sendMessage(String queueUrl, String msg);

    List<Message> receiveMessage(String topicUrl);

    void deleteMessage(String topicUrl, String handleReceipt);

}
