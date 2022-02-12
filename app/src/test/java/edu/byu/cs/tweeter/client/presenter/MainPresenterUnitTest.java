package edu.byu.cs.tweeter.client.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {
    private MainPresenter.MainView mockView;
    private StatusService mockStatusService;
    //private Cache mockCache;

    private MainPresenter mainPresenterSpy;

    private AuthToken mockAuthToken;
    private Status mockStatus;

    private AuthToken authToken;
    private Status status;
    private User user;

    @Before
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockAuthToken = Mockito.mock(AuthToken.class);
        mockStatus = Mockito.mock(Status.class);
        //mockCache = Mockito.mock(Cache.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void testPost_checkPassedInParams() {
        authToken = new AuthToken("authToken", "today");
        user= new User("firstName", "lastName", "alias", "imageURL");
        status = new Status("post", user, "today", null, null);

        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken passedAuthToken = invocation.getArgument(0, AuthToken.class);
                Status passedStatus = invocation.getArgument(1, Status.class);

                Assert.assertTrue(passedStatus.equals(status));
                Assert.assertTrue(passedAuthToken.getToken().equals(authToken.getToken()));
                Assert.assertTrue(passedAuthToken.getDatetime().equals(authToken.getDatetime()));
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mainPresenterSpy).post(Mockito.any(), Mockito.any());
        mainPresenterSpy.post(authToken, status);
    }

    @Test
    public void testPost_success() {

        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.handlePostSuccess();
                return null;
            }
        };

        postCall(answer);

        Mockito.verify(mockView).postDone();
    }

    @Test
    public void testPost_failure() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {

                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.handleFailure("Post request failed: message");
                return null;
            }
        };

        postCall(answer);

        Mockito.verify(mockView, Mockito.times(0)).postDone();
        Mockito.verify(mockView).displayMessage("Post request failed: message");
    }

    @Test
    public void testPost_exception() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.StatusObserver observer = invocation.getArgument(2, StatusService.StatusObserver.class);
                observer.handleException("Exception during post request: message", new Exception("exception message"));
                return null;
            }
        };

        postCall(answer);

        Mockito.verify(mockView, Mockito.times(0)).postDone();
        Mockito.verify(mockView).displayMessage("Exception during post request: message");

    }

    private void postCall(Answer answer) {
        Mockito.doAnswer(answer).when(mockStatusService).post(Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.post(mockAuthToken, mockStatus);
        Mockito.verify(mockView).displayMessage("Posting Status...");
    }

}
