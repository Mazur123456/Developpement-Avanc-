package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.CategoryService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet pour modifier une annonce
 */
@WebServlet("/annonce/edit")
public class AnnonceEditServlet extends HttpServlet {

    private static final String PATH_ANNONCES = "/annonces";
    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + PATH_ANNONCES);
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Optional<Annonce> annonceOpt = annonceService.findByIdWithDetails(id);

            if (annonceOpt.isPresent()) {
                List<Category> categories = categoryService.findAll();
                request.setAttribute("annonce", annonceOpt.get());
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("/WEB-INF/views/annonces/edit.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + PATH_ANNONCES);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + PATH_ANNONCES);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("userId");

        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        String categoryIdStr = request.getParameter("categoryId");

        try {
            Long id = Long.parseLong(idParam);
            Long categoryId = null;
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Long.parseLong(categoryIdStr);
            }

            annonceService.update(id, title, description, adress, mail, categoryId, userId);
            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + id);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (ValidationUtil.ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute("error", "Veuillez corriger les erreurs du formulaire");

            // Recharger les données nécessaires pour la vue
            try {
                Long id = Long.parseLong(idParam);
                request.setAttribute("annonce", new Annonce(title, description, adress, mail)); // Objet temporaire pour
                                                                                                // l'affichage
                // Note: L'idéal serait de renrenvoyer l'objet modifié mais non persisté, ou de
                // passer les champs individuels comme pour Create
                // Ici on simplifie en rechargeant les catégories mais on doit gérer l'affichage
                // des valeurs saisies
                request.setAttribute("id", id);
                request.setAttribute("categories", categoryService.findAll());

                // On passe les valeurs brutes pour que la JSP les réaffiche (si elle utilise
                // param ou attributes)
                request.setAttribute("title", title);
                request.setAttribute("description", description);
                request.setAttribute("adress", adress);
                request.setAttribute("mail", mail);
                request.setAttribute("categoryId", categoryIdStr);

                request.getRequestDispatcher("/WEB-INF/views/annonces/edit.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + PATH_ANNONCES);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }
}
