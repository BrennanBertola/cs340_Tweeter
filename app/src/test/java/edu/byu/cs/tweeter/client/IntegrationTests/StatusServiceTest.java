package edu.byu.cs.tweeter.client.IntegrationTests;

import android.os.Looper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.net.ServerFacade;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StatusServiceTest {
    private StatusService serviceSpy;
    private GetStoryTask mockTask;
    private StatusService.GetStoryHandler handlerSpy;
    private ServerFacade serverFacadeMock;

    private AuthToken authToken = new AuthToken();
    private User user = new User("test", "test", "test", "test");
    private Status status = new Status();

//    @Before
//    public void setup() {
//        serviceSpy = Mockito.spy(new StatusService());
//        //handlerSpy = Mockito.spy(new StatusService.GetStoryHandler(null));
//        serverFacadeMock = Mockito.mock(ServerFacade.class);
//        mockTask = Mockito.mock(GetStoryTask.class);
//
//
//
//        Mockito.when(serviceSpy.getGetStoryTask(authToken, user, 10, status, null)).thenReturn(mockTask);
//    }
//
//    @Test
//    public void GetStory_Success () {
//        StoryResponse response = new StoryResponse(null, false);
//        try {
//            Mockito.when(serverFacadeMock.getStory(Mockito.any(), Mockito.any())).thenReturn(response);
//            serviceSpy.getStory(null, null, 10, null, null);
//        }
//        catch (IOException | TweeterRemoteException ex) {
//            Assert.assertTrue(false); //causes test to fail;
//        }
//
//    }
}
