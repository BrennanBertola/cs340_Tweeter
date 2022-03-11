package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusDAO {
    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTarget() != null;

        List<Status> allPosts = getDummyFeed();
        List<Status> responsePosts = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allPosts != null) {
                int feedIndex = getStartingIndex(request.getLast(), allPosts);

                for(int limitCounter = 0; feedIndex < allPosts.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                    responsePosts.add(allPosts.get(feedIndex));
                }

                hasMorePages = feedIndex < allPosts.size();
            }
        }

        return new FeedResponse(responsePosts, hasMorePages);
    }

    public StoryResponse getStory(StoryRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTarget() != null;

        List<Status> allPosts = getDummyStory();
        List<Status> responsePosts = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allPosts != null) {
                int storyIndex = getStartingIndex(request.getLast(), allPosts);

                for(int limitCounter = 0; storyIndex < allPosts.size() && limitCounter < request.getLimit(); storyIndex++, limitCounter++) {
                    responsePosts.add(allPosts.get(storyIndex));
                }

                hasMorePages = storyIndex < allPosts.size();
            }
        }

        return new StoryResponse(responsePosts, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse(true);
    }

    private int getStartingIndex(String lastPost, List<Status> allPosts) {
        int index = 0;

        if(lastPost != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allPosts.size(); i++) {
                if(lastPost.equals(allPosts.get(i).getPost())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    index = i + 1;
                    break;
                }
            }
        }

        return index;
    }

    private List<Status> getDummyFeed() {return getFakeData().getFakeStatuses();}
    private List<Status> getDummyStory() {return getFakeData().getFakeStatuses();}

    private FakeData getFakeData() {return new FakeData();}
}
