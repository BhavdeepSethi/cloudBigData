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
import edu.columbia.cbd.models.Sentiment;
import edu.columbia.cbd.models.Sentiment.SentimentLabel;
import edu.columbia.cbd.models.Tweet;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

/**
 * Created by bhavdeepsethi on 10/30/14.
 */
public class TweetAnalyzeExecutor implements Runnable{
	
	private Tweet tweet;
	private String SQSArn;
	private String SNSArn;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" Start. Thread");
		processTweet();
        System.out.println(Thread.currentThread().getName()+" End.");
    
	}
	
	public TweetAnalyzeExecutor(Tweet tweet, String SQSArn, String SNSArn){
		this.tweet=tweet;
		this.SNSArn=SNSArn;
		this.SQSArn=SQSArn;
	}
	
	private void processTweet() {
        	StringBuffer URLParameters= new StringBuffer();
            URLParameters.append("apikey="+Constants.ALCHEMY_API_KEY);
            URLParameters.append("&");
            URLParameters.append("text="+tweet.getTweet().trim());
            String excutePost = HttpRequestHandler.excutePost(Constants.ALCHEMY_URL, URLParameters.toString());
            Map map = (Map)(new Gson().fromJson(excutePost, Map.class)).get("docSentiment");
            String type =(String)map.get("type");
    		double score = Double.parseDouble((String) map.get("score"));
    		Sentiment sentiment;
    		if(type.toLowerCase().contains("positive"))
    			sentiment= new Sentiment(SentimentLabel.POSITIVE, score);
    		else
    			sentiment= new Sentiment(SentimentLabel.NEGATIVE, score);
    		tweet.setSentiment(sentiment);
    		
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
                System.out.println("Tweet caught:"+msg.getBody());
                String text = msg.getBody();
                
                //Convert to thread pool and Do Alchemy Work Here
                
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(text, Tweet.class);
                Runnable TweetAnalyzeExecutor = new TweetAnalyzeExecutor(tweet,"","");
                executor.execute(TweetAnalyzeExecutor);
                
                sqsServiceIncoming.deleteMessage(Constants.TWITTER_QUEUE_URL, msg.getReceiptHandle());
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Finished all threads");
        }


    }

	

}
