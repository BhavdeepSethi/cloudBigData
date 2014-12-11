package edu.columbia.workers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.sqs.model.Message;
import com.columbia.cbd.utils.HttpRequestHandler;
import com.google.gson.Gson;

import edu.columbia.cbd.BootStrap;
import edu.columbia.cbd.WorkerBootStrap;
import edu.columbia.cbd.models.Constants;
import edu.columbia.cbd.models.Sentiment;
import edu.columbia.cbd.models.Tweet;
import edu.columbia.cbd.models.Sentiment.SentimentLabel;
import edu.columbia.cbd.service.MongoService;
import edu.columbia.cbd.service.SNSService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.MongoServiceImpl;
import edu.columbia.cbd.service.impl.SNSServiceImpl;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

public class UpdatedTweetFeederExecutor implements Runnable{
	private Tweet tweet;
	private MongoService mongoService;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" Start. Thread");
		updateMongo();
        System.out.println(Thread.currentThread().getName()+" End.");
    
	}
	public UpdatedTweetFeederExecutor(Tweet tweet, MongoService mongoService){
		this.tweet=tweet;
		
	}

	private void updateMongo() {
    	mongoService.updateTweet(tweet);
	}
	
    public static void main(String[] args) {
        System.out.println("Starting Tweet Updation now!");
        WorkerBootStrap workerBootStrap = WorkerBootStrap.getInstance();
        workerBootStrap.startUp();
        SQSService sqsServiceIncoming = new SQSServiceImpl();
        MongoService mongoService = new MongoServiceImpl();
        Gson gson = new Gson();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        while(true) {
            List<Message> msgList = sqsServiceIncoming.receiveMessage(Constants.TWITTER_OUTGOING_QUEUE_URL);
            for (Message msg : msgList) {
                System.out.println("Tweet caught:"+msg.getBody());
                String text = msg.getBody();
                
                //Convert to thread pool 
               
                Tweet tweet = gson.fromJson(text, Tweet.class);
                Runnable UpdatedTweetFeederExecutor = new UpdatedTweetFeederExecutor(tweet,mongoService);
                executor.execute(UpdatedTweetFeederExecutor);   
                sqsServiceIncoming.deleteMessage(Constants.TWITTER_OUTGOING_QUEUE_URL, msg.getReceiptHandle());
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Finished all threads");
        }


    }
}
