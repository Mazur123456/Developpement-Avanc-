package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.InvalidStateException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet unifiée pour les changements de statut d'une annonce.
 * Gère les actions "publish" (DRAFT → PUBLISHED) et "archive" (PUBLISHED →
 * ARCHIVED).
 * Remplace les anciens AnnoncePublishServlet et AnnonceArchiveServlet (code
 * quasi-identique).
 */
@WebServlet("/annonce/status")
public class AnnonceStatusServlet extends HttpServlet {

    private static final String ACTION_PUBLISH = "publish";
    private static final String ACTION_ARCHIVE = "archive";
    private final AnnonceService annonceService = new AnnonceService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("userId");

        String idParam = request.getParameter("id");
        String action = request.getParameter("action");

        if (idParam == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/annonces");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);

            switch (action) {
                case ACTION_PUBLISH:
                    annonceService.publish(id, userId);
                    break;
                case ACTION_ARCHIVE:
                    annonceService.archive(id, userId);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/annonces");
                    return;
            }

            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + id);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (InvalidStateException e) {
            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + idParam);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/annonces");
        }
    }
}
