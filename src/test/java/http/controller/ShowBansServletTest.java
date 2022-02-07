package http.controller;


import org.apache.openejb.testing.Classes;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import rocks.limburg.cdimock.CdiMock;
import service.BanService;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@Classes(cdi = true,
        value={ShowBansServlet.class},
        cdiStereotypes = CdiMock.class)
public class ShowBansServletTest extends ServletTest{

    @Mock private BanService service;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Inject ShowBansServlet showBansServlet;

    @Test
    void successfulldoPost() throws ServletException, IOException{
        when(request.getParameter("userId")).thenReturn("1");
        showBansServlet.doPost(request,response);
        verify(service, times(1)).retrieveUserBan(anyInt());
    }

    @Test
    void faildoPost() throws ServletException, IOException{
        when(request.getParameter("userId")).thenReturn("1");
        doThrow(ConstraintViolationException.class).when(service).retrieveUserBan(anyInt());
        assertThrows(ConstraintViolationException.class,() -> showBansServlet.doPost(request,response));
    }


    @Test
    void successfulldoGet() throws ServletException, IOException{
        when(request.getParameter("userId")).thenReturn("1");
        showBansServlet.doGet(request,response);
        verify(service, times(1)).retrieveUserBan(anyInt());
    }

    @Test
    void faildoGet() throws ServletException, IOException{
        when(request.getParameter("userId")).thenReturn("1");
        doThrow(ConstraintViolationException.class).when(service).retrieveUserBan(anyInt());
        assertThrows(ConstraintViolationException.class,() -> showBansServlet.doGet(request,response));
    }


}