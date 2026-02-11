package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet pour supprimer une annonce (avec vérification de propriété)
 */
@WebServlet("/annonce/delete")
public class AnnonceDeleteServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("userId");

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/annonces");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            annonceService.delete(id, userId);
            response.sendRedirect(request.getContextPath() + "/annonces");
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/annonces");
        }
    }
}
