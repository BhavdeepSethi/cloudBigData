package edu.columbia.workers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.sqs.model.Message;
import com.columbia.cbd.utils.HttpRequestHandler;
import com.google.gson.Gson;

import edu.columbia.cbd.BootStrap;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

/**
 * Created by bhavdeepsethi on 10/30/14.
 */
public class TweetAnalyzeExecutor implements Runnable{
	
	private String id;
	private String tweetText;
	private String SQSArn;
	private String SNSArn;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" Start. Thread" +id);
		processTweet();
        System.out.println(Thread.currentThread().getName()+" End.");
    
	}
	
	public TweetAnalyzeExecutor(String id,String text, String SQSArn, String SNSArn){
		this.id=id;
		tweetText=text;
		this.SNSArn=SNSArn;
		this.SQSArn=SQSArn;
	}
	
	private void processTweet() {
        	StringBuffer URLParameters= new StringBuffer();
            URLParameters.append("apikey="+Constants.ALCHEMY_API_KEY);
            URLParameters.append("&");
            URLParameters.append("text="+tweetText.trim());
            String excutePost = HttpRequestHandler.excutePost(Constants.ALCHEMY_URL, URLParameters.toString());
            Map map = (Map)(new Gson().fromJson(excutePost, Map.class)).get("docSentiment");
            String type =(String)map.get("type");
    		double score = Double.parseDouble((String) map.get("score"));
    }

    public static void main(String[] args) {
        System.out.println("Starting Tweet Analyze now!");
        BootStrap bootStrap = BootStrap.getInstance();
        bootStrap.startUp();

        SQSService sqsServiceIncoming = new SQSServiceImpl();
        SQSService sqsServiceOutgoing = new SQSServiceImpl();
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        while(true) {
            List<Message> msgList = sqsServiceIncoming.receiveMessage(Constants.TWITTER_QUEUE_URL);
            for (Message msg : msgList) {
                System.out.println(msg.getBody());
                String msgId = msg.getMessageId();
                String text = msg.getBody();
                
                //Convert to thread pool and Do Alchemy Work Here
                Runnable TweetAnalyzeExecutor = new TweetAnalyzeExecutor(msgId,text,"","");
                executor.execute(TweetAnalyzeExecutor);
                
                sqsServiceIncoming.deleteMessage(Constants.TWITTER_QUEUE_URL, msg.getReceiptHandle());
            }
            while (!executor.isTerminated()) {
            }
            System.out.println("Finished all threads");
        }


    }

	

}
