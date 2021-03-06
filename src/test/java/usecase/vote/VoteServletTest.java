package usecase.vote;


import org.apache.openejb.testing.Classes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import rocks.limburg.cdimock.CdiMock;
import usecase.ServletTest;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@Classes(cdi = true,
        value={VoteServlet.class},
        cdiStereotypes = CdiMock.class)
public class VoteServletTest extends ServletTest {

    @Mock private VoteService service;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Inject VoteServlet voteServlet;


    @ParameterizedTest
    @CsvSource({"post,upvote", "post,downvote", "comment,upvote", "comment,downvote"})
    void successfulldoPost(String type, String vote) throws ServletException, IOException{
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn(type);
        when(request.getParameter("vote")).thenReturn(vote);

        voteServlet.doPost(request,response);

        if(type.equalsIgnoreCase("post") ){
            if(vote.equalsIgnoreCase("upvote"))
                verify(service, times(1)).upvotePost(anyInt());
            else if(vote.equalsIgnoreCase("downvote"))
                verify(service, times(1)).downvotePost(anyInt());
        }else if(type.equalsIgnoreCase("comment")){
            if(vote.equalsIgnoreCase("upvote"))
                verify(service, times(1)).upvoteComment(anyInt());
            else if(vote.equalsIgnoreCase("downvote"))
                verify(service, times(1)).downvoteComment(anyInt());
        }
    }

    @ParameterizedTest
    @CsvSource({"post,upvote", "post,downvote", "comment,upvote", "comment,downvote"})
    void faildoPost(String type, String vote) throws ServletException, IOException{
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn(type);
        when(request.getParameter("vote")).thenReturn(vote);

        doThrow(ConstraintViolationException.class).when(service).upvotePost(anyInt());
        doThrow(ConstraintViolationException.class).when(service).downvotePost(anyInt());
        doThrow(ConstraintViolationException.class).when(service).upvoteComment(anyInt());
        doThrow(ConstraintViolationException.class).when(service).downvoteComment(anyInt());
        assertThrows(ConstraintViolationException.class,() -> voteServlet.doPost(request,response));
    }

    @ParameterizedTest
    @CsvSource({"post,upvote", "post,downvote", "comment,upvote", "comment,downvote"})
    void successfulldoGet(String type, String vote) throws ServletException, IOException{
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn(type);
        when(request.getParameter("vote")).thenReturn(vote);

        voteServlet.doGet(request,response);

        if(type.equalsIgnoreCase("post") ){
            if(vote.equalsIgnoreCase("upvote"))
                verify(service, times(1)).upvotePost(anyInt());
            else if(vote.equalsIgnoreCase("downvote"))
                verify(service, times(1)).downvotePost(anyInt());
        }else if(type.equalsIgnoreCase("comment")){
            if(vote.equalsIgnoreCase("upvote"))
                verify(service, times(1)).upvoteComment(anyInt());
            else if(vote.equalsIgnoreCase("downvote"))
                verify(service, times(1)).downvoteComment(anyInt());
        }
    }

    @ParameterizedTest
    @CsvSource({"post,upvote", "post,downvote", "comment,upvote", "comment,downvote"})
    void faildoGet(String type, String vote) throws ServletException, IOException{
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn(type);
        when(request.getParameter("vote")).thenReturn(vote);

        doThrow(ConstraintViolationException.class).when(service).upvotePost(anyInt());
        doThrow(ConstraintViolationException.class).when(service).downvotePost(anyInt());
        doThrow(ConstraintViolationException.class).when(service).upvoteComment(anyInt());
        doThrow(ConstraintViolationException.class).when(service).downvoteComment(anyInt());
        assertThrows(ConstraintViolationException.class,() -> voteServlet.doGet(request,response));
    }

    @Test
    void emptyTypedoGet(){
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn(null);
        when(request.getParameter("vote")).thenReturn("upvote");

        assertThrows(IllegalArgumentException.class,() -> voteServlet.doGet(request,response));
    }

    @Test
    void emptyPostVotedoGet(){
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn("post");
        when(request.getParameter("vote")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> voteServlet.doGet(request,response));
    }

    @Test
    void emptyCommentVotedoGet(){
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("type")).thenReturn("comment");
        when(request.getParameter("vote")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> voteServlet.doGet(request,response));
    }

}