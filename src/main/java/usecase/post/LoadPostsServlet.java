package usecase.post;

import common.http.ParameterConverter;
import common.http.interceptor.InterceptableServlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static usecase.post.PostSearchForm.SortCriteria.*;

/**
 * Classe che permette di visualizzare i post.
 */
@WebServlet("/loadposts")
class LoadPostsServlet extends InterceptableServlet {
    @Inject private PostService postService;

    private static final Function<LocalDate, Instant> LOCALDATE_TO_INSTANT =
            x -> x.atStartOfDay().toInstant(ZoneOffset.UTC);

    private static final Map<String, PostSearchForm.SortCriteria> SORT_CRITERIA = Map.of(
            "oldest", OLDEST,
            "newest", NEWEST,
            "mostvoted", MOSTVOTED);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ParameterConverter converter = new ParameterConverter(req);
        String content = req.getParameter("content");
        String section = req.getParameter("section");
        String author = req.getParameter("author");
        Instant postedAfter = converter.getDateParameter("postedAfter")
                .map(LOCALDATE_TO_INSTANT).orElse(null);
        Instant postedBefore = converter.getDateParameter("postedBefore")
                .map(LOCALDATE_TO_INSTANT).orElse(null);
        PostSearchForm.SortCriteria orderBy = SORT_CRITERIA.get(req.getParameter("orderby"));
        int page = converter.getIntParameter("page").orElse(1);
        boolean onlyFollow = "on".equals(req.getParameter("onlyfollow"));
        boolean includeBody = req.getParameter("includeBody") != null;

        PostSearchForm postSearchForm = PostSearchForm.builder()
                .content(content)
                .onlyFollow(onlyFollow)
                .includeBody(includeBody)
                .sectionName(section)
                .authorName(author)
                .orderBy(orderBy)
                .postedAfter(postedAfter)
                .postedBefore(postedBefore)
                .page(page)
                .build();

        List<PostPage> posts = postService.loadPosts(postSearchForm);
        req.setAttribute("posts",posts);
        req.getRequestDispatcher("/WEB-INF/views/partials/post-previews.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
