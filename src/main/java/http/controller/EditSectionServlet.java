package http.controller;

import http.controller.interceptor.AuthorizationConstraints;
import http.controller.interceptor.ForwardOnError;
import http.util.ParameterConverter;
import http.util.interceptor.InterceptableServlet;
import service.SectionService;
import service.dto.SectionPage;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.IOException;

import static http.controller.interceptor.AuthorizationConstraints.Types.ADMINS_ONLY;

@WebServlet("/admin/editsection")
@MultipartConfig
@AuthorizationConstraints(ADMINS_ONLY)
public class EditSectionServlet extends InterceptableServlet {
    @Inject private SectionService service;

    private static final String EDIT_SECTION_PAGE = "/WEB-INF/views/crm/edit-section.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ParameterConverter converter = new ParameterConverter(request);
        int sectionId = converter.getIntParameter("sectionId").orElse(0);
        SectionPage section = service.showSection(sectionId);
        request.setAttribute("section", section);
        request.getRequestDispatcher(EDIT_SECTION_PAGE).forward(request, response);
    }

    @Override
    @ForwardOnError(EDIT_SECTION_PAGE)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ParameterConverter converter = new ParameterConverter(request);
        int sectionId = converter.getIntParameter("sectionId").orElse(0);
        String description = request.getParameter("description");
        Part picture = request.getPart("picture");
        Part banner = request.getPart("banner");

        SectionPage sectionPage = new SectionPage(sectionId,null,description,picture.getName(),banner.getName(),0,false); //false?
        BufferedInputStream buffPicture = new BufferedInputStream(picture.getInputStream());
        BufferedInputStream buffBanner = new BufferedInputStream(banner.getInputStream());
        service.editSection(sectionPage,sectionId,buffPicture,buffBanner);
        response.sendRedirect(request.getContextPath()+"/admin/showsections");
    }
}