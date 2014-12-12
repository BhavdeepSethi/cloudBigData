package edu.columbia.workers;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.conditions.ConditionFactory;
import edu.columbia.cbd.models.Constants;
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

        Statement statement = new Statement(Effect.Allow)
                .withActions(SQSActions.SendMessage)
                .withPrincipals(new Principal("*"))
                .withConditions(ConditionFactory.newSourceArnCondition(Constants.TWITTER_TOPIC_ARN))
                .withResources(new Resource(Constants.TWITTER_OUTGOING_QUEUE_ARN));

        Policy policy = new Policy("MySQSPolicy001")
                .withStatements(statement);


        sqsServiceOutgoing.setAttribute(policy, Constants.TWITTER_OUTGOING_QUEUE_URL);
        //Give users permissions to the appropriate topic and queue actions

		//subscribe sqs 
		Constants.TWITTER_SQS_SUBSCRIPTION_ARN=snsService.subscribeTopic(Constants.TWITTER_TOPIC_ARN, "sqs", Constants.TWITTER_OUTGOING_QUEUE_ARN);
				
		//Give permission to the Amazon SNS topic to send messages to the Amazon SQS queue

    }

}
