package edu.columbia.workers;

import com.amazonaws.services.sqs.model.Message;
import edu.columbia.cbd.BootStrap;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

import java.util.List;

/**
 * Created by bhavdeepsethi on 10/30/14.
 */
public class TweetAnalyzeExecutor {



    public static void main(String[] args) {
        System.out.println("Starting Tweet Analyze now!");
        BootStrap bootStrap = BootStrap.getInstance();
        bootStrap.startUp();

        SQSService sqsService = new SQSServiceImpl();

        while(true) {
            List<Message> msgList = sqsService.receiveMessage(Constants.TWITTER_QUEUE_URL);
            for (Message msg : msgList) {
                System.out.println(msg.getBody());
                String msgId = msg.getMessageId();

                //Convert to thread pool and Do Alchemy Work Here

                sqsService.deleteMessage(Constants.TWITTER_QUEUE_URL, msg.getReceiptHandle());
            }
        }


    }

}
