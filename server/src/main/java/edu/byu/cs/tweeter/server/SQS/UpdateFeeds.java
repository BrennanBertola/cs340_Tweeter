package edu.byu.cs.tweeter.server.SQS;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void>{
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Gson gson = new GsonBuilder().create();
            FeedMessage feedMessage = gson.fromJson(msg.getBody(), FeedMessage.class);
            if (feedMessage != null) {
                StatusService service = new StatusService(new DynamoDAOFactory());
                service.updateFeeds(feedMessage);
            }
        }
        return null;
    }
}
