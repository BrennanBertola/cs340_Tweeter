package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FolloweeCountRequest;
import edu.byu.cs.tweeter.model.net.response.FolloweeCountResponse;
import edu.byu.cs.tweeter.server.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class FolloweeCountHandler implements RequestHandler<FolloweeCountRequest, FolloweeCountResponse> {

    @Override
    public FolloweeCountResponse handleRequest(FolloweeCountRequest request, Context context) {
        FollowService service = new FollowService(new DynamoDAOFactory());
        return service.getFolloweeCount(request);
    }
}

