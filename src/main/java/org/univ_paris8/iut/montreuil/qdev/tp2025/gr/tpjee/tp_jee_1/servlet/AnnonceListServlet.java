package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet pour afficher la liste des annonces avec pagination
 */
@WebServlet("/annonces")
public class AnnonceListServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer le numéro de page
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1)
                    page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Récupérer les annonces publiées avec pagination
        List<Annonce> annonces = annonceService.findPublished(page);
        int totalPages = annonceService.getTotalPages();

        // Récupérer les catégories pour le filtre
        List<Category> categories = categoryService.findAll();

        request.setAttribute("annonces", annonces);
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/annonces/list.jsp").forward(request, response);
    }
}
