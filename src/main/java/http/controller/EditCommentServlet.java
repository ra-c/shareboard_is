package http.controller;

import http.controller.interceptor.AuthorizationConstraints;
import http.util.ParameterConverter;
import http.util.interceptor.InterceptableServlet;
import service.CommentService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static http.controller.interceptor.AuthorizationConstraints.Types.REQUIRE_AUTHENTICATION;

@WebServlet("/editcomment")
@AuthorizationConstraints(REQUIRE_AUTHENTICATION)
public class EditCommentServlet extends InterceptableServlet {
    @Inject private CommentService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ParameterConverter converter = new ParameterConverter(request);
        int commentId = converter.getIntParameter("id").orElse(0);
        String text = request.getParameter("text");
        service.editComment(commentId,text);

        int parentPostId = service.getComment(commentId).getPostId();

        response.sendRedirect( getServletContext().getContextPath() + "/post/" + parentPostId);
    }
}
