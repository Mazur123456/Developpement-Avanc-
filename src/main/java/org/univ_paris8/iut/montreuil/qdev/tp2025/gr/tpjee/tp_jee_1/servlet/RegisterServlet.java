package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.UserService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet pour l'inscription des utilisateurs
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String ATTR_ERROR = "error";
    private static final String PARAM_USERNAME = "username";
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            request.setAttribute(ATTR_ERROR, "Veuillez remplir tous les champs");
            forwardWithValues(request, response, username, email);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute(ATTR_ERROR, "Les mots de passe ne correspondent pas");
            forwardWithValues(request, response, username, email);
            return;
        }

        try {
            User user = userService.register(username, email, password);

            // Connexion automatique après inscription
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute(PARAM_USERNAME, user.getUsername());

            response.sendRedirect("annonces");
        } catch (ValidationUtil.ValidationException e) {
            request.setAttribute("errors", e.getErrors());
            request.setAttribute(ATTR_ERROR, "Erreur de validation");
            forwardWithValues(request, response, username, email);
        } catch (DuplicateEntityException e) {
            request.setAttribute(ATTR_ERROR, e.getMessage());
            forwardWithValues(request, response, username, email);
        }
    }

    private void forwardWithValues(HttpServletRequest request, HttpServletResponse response,
            String username, String email)
            throws ServletException, IOException {
        request.setAttribute(PARAM_USERNAME, username);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
}
