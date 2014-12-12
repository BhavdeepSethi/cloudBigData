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
import edu.columbia.cbd.service.SNSService;
import edu.columbia.cbd.service.SQSService;
import edu.columbia.cbd.service.impl.SNSServiceImpl;
import edu.columbia.cbd.service.impl.SQSServiceImpl;

/*
Author: Diwakar Mahajan (@diwakar21)
 */

public class TweetAnalyzeExecutor implements Runnable{

    private Tweet tweet;
    private SNSService snsService;
    private static int WAIT_TIME = 1000;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        //System.out.println(Thread.currentThread().getName()+" Start. Thread");
        processTweet();
        //System.out.println(Thread.currentThread().getName()+" End.");

    }

    public TweetAnalyzeExecutor(Tweet tweet, SNSService snsService){
        this.tweet=tweet;
        this.snsService = snsService;
    }

    private void processTweet() {
        try {
            StringBuffer URLParameters = new StringBuffer();
            URLParameters.append("apikey=" + Constants.ALCHEMY_API_KEY);
            URLParameters.append("&");
            URLParameters.append("text=" + tweet.getTweet().trim());
            URLParameters.append("&").append("outputMode=json");
            String excutePost = new HttpRequestHandler().excutePost(Constants.ALCHEMY_URL, URLParameters.toString());

            Map map = (Map) (new Gson().fromJson(excutePost, Map.class)).get("docSentiment");
            String type = (String) map.get("type");
            String scoreString = (String) map.get("score");
            double score = 0.0;
            if(scoreString != null)
                score = Double.parseDouble(scoreString);

            Sentiment sentiment;
            if (type.toLowerCase().contains("positive"))
                sentiment = new Sentiment(SentimentLabel.POSITIVE, score);
            else if(type.toLowerCase().contains("negative"))
                sentiment = new Sentiment(SentimentLabel.NEGATIVE, score);
            else{
                return;
            }
            tweet.setSentiment(sentiment);
            String tweetJson = new Gson().toJson(tweet);
            snsService.sendMessage(Constants.TWITTER_TOPIC_ARN, tweetJson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Tweet Analyze now!");
        BootStrap bootStrap = BootStrap.getInstance();
        bootStrap.startUp();
        WorkerBootStrap workerBootStrap = WorkerBootStrap.getInstance();
        workerBootStrap.startUp();
        SQSService sqsServiceIncoming = new SQSServiceImpl();
        SNSService snsService = new SNSServiceImpl();
        Gson gson = new Gson();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        while(true) {
            boolean worked = false;
            List<Message> msgList = sqsServiceIncoming.receiveMessage(Constants.TWITTER_QUEUE_URL);
            for (Message msg : msgList) {
                worked = true;
                WAIT_TIME = 1000;
                //System.out.println("Tweet caught:"+msg.getBody());
                String text = msg.getBody();

                //Convert to thread pool and Do Alchemy Work Here
                Tweet tweet = gson.fromJson(text, Tweet.class);

                Runnable TweetAnalyzeExecutor = new TweetAnalyzeExecutor(tweet, snsService);
                executor.execute(TweetAnalyzeExecutor);

                sqsServiceIncoming.deleteMessage(Constants.TWITTER_QUEUE_URL, msg.getReceiptHandle());
            }

            if(!worked){
                System.out.println("Waiting for "+WAIT_TIME/1000 +" seconds");
                Thread.sleep(WAIT_TIME);
                WAIT_TIME = WAIT_TIME * 2;
                if(WAIT_TIME > 64000){
                    WAIT_TIME =  64000;
                }

            }
            /*executor.shutdown();
            while (!executor.isTerminated()) {
            }
            */
            //break;

        }



    }



}
