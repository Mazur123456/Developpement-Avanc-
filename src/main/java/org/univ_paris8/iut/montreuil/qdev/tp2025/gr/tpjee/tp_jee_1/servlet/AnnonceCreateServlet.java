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

/**
 * Servlet pour créer une nouvelle annonce
 */
@WebServlet("/annonce/create")
public class AnnonceCreateServlet extends HttpServlet {

    private static final String ATTR_ERROR = "error";
    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/annonces/create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        String categoryIdStr = request.getParameter("categoryId");

        // Validation
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute(ATTR_ERROR, "Le titre est obligatoire");
            forwardWithValues(request, response, title, description, adress, mail, categoryIdStr);
            return;
        }

        try {
            Long categoryId = null;
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Long.parseLong(categoryIdStr);
            }

            Annonce annonce = annonceService.create(title, description, adress, mail, userId, categoryId);
            response.sendRedirect(request.getContextPath() + "/annonce/detail?id=" + annonce.getId());
        } catch (ValidationUtil.ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute(ATTR_ERROR, "Veuillez corriger les erreurs du formulaire");
            forwardWithValues(request, response, title, description, adress, mail, categoryIdStr);
        } catch (Exception e) {
            request.setAttribute(ATTR_ERROR, "Erreur lors de la création: " + e.getMessage());
            forwardWithValues(request, response, title, description, adress, mail, categoryIdStr);
        }
    }

    private void forwardWithValues(HttpServletRequest request, HttpServletResponse response,
            String title, String description, String adress,
            String mail, String categoryId)
            throws ServletException, IOException {
        request.setAttribute("title", title);
        request.setAttribute("description", description);
        request.setAttribute("adress", adress);
        request.setAttribute("mail", mail);
        request.setAttribute("categoryId", categoryId);
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/annonces/create.jsp").forward(request, response);
    }
}
