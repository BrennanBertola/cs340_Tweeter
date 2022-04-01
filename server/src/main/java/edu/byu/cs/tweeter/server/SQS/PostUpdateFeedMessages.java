package edu.byu.cs.tweeter.server.SQS;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void>{
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Gson gson = new GsonBuilder().create();
            Status post = gson.fromJson(msg.getBody(), Status.class);
            if (post != null) {
                FollowService service = new FollowService(new DynamoDAOFactory());
                service.postUpdateFeedMessages(post);
            }
        }
        return null;
    }
}
