package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet pour la connexion des utilisateurs
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Vérifier si déjà connecté
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("annonces");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validation basique
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        // Authentification
        Optional<User> userOpt = userService.authenticate(username, password);

        if (userOpt.isPresent()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userOpt.get());
            session.setAttribute("userId", userOpt.get().getId());
            session.setAttribute("username", userOpt.get().getUsername());

            // Rediriger vers la page demandée ou la liste des annonces
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect("annonces");
            }
        } else {
            request.setAttribute("error", "Identifiants incorrects");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
