package usecase.auth;


import org.apache.openejb.testing.Classes;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import rocks.limburg.cdimock.CdiMock;
import usecase.ServletTest;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@Classes(cdi = true,
        value={LogoutServlet.class},
        cdiStereotypes = CdiMock.class)
public class LogoutServletServletTest extends ServletTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher dispatcher;
    @Inject
    LogoutServlet logoutServlet;


    @Test
    void successfulldoGet() throws ServletException, IOException{
        when(request.getSession(true)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        LogoutServlet spyServlet = Mockito.spy(logoutServlet);
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("path");
        doReturn(servletContext).when(spyServlet).getServletContext();
        logoutServlet.doGet(request,response);
        verify(response, times(1)).sendRedirect(any());
    }

    @Test
    void faildoGet() throws ServletException, IOException{
        when(request.getSession(true)).thenReturn(session);
        doThrow(ConstraintViolationException.class).when(response).sendRedirect(any());
        assertThrows(ConstraintViolationException.class,() -> logoutServlet.doGet(request,response));
    }


}