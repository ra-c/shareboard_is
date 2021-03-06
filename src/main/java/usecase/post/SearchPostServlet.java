package usecase.post;

import common.http.interceptor.InterceptableServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet relativa alla ricerca di un post.
 */
@WebServlet("/search")
class SearchPostServlet extends InterceptableServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //la ricerca dei post è delegata a LoadPostsServlet. Il caricamento è fatto via AJAX.
        req.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}