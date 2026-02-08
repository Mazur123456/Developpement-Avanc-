package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet pour afficher le détail d'une annonce
 */
@WebServlet("/annonce/detail")
public class AnnonceDetailServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("annonces");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Annonce> annonceOpt = annonceService.findByIdWithDetails(id);

            if (annonceOpt.isPresent()) {
                request.setAttribute("annonce", annonceOpt.get());
                request.getRequestDispatcher("/WEB-INF/views/annonces/detail.jsp").forward(request, response);
            } else {
                response.sendRedirect("annonces");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("annonces");
        }
    }
}
