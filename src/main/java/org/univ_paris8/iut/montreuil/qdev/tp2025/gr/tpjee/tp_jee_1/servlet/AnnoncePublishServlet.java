package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour publier une annonce (DRAFT → PUBLISHED)
 */
@WebServlet("/annonce/publish")
public class AnnoncePublishServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/annonces");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            annonceService.publish(id);
            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + id);
        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + idParam);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/annonces");
        }
    }
}
