package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour gérer les erreurs HTTP (404, 500, etc.)
 */
@WebServlet("/error")
public class ErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleError(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les informations d'erreur
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        if (statusCode == null) {
            statusCode = 500;
        }

        // Définir les attributs pour la page d'erreur
        request.setAttribute("statusCode", statusCode);
        request.setAttribute("errorMessage", getErrorMessage(statusCode, errorMessage));
        request.setAttribute("requestUri", requestUri);

        if (throwable != null) {
            request.setAttribute("exceptionMessage", throwable.getMessage());
        }

        // Afficher la page d'erreur
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }

    private String getErrorMessage(int statusCode, String defaultMessage) {
        switch (statusCode) {
            case 400:
                return "Requête invalide";
            case 401:
                return "Non autorisé";
            case 403:
                return "Accès interdit";
            case 404:
                return "Page non trouvée";
            case 405:
                return "Méthode non autorisée";
            case 500:
                return "Erreur interne du serveur";
            case 503:
                return "Service temporairement indisponible";
            default:
                return defaultMessage != null ? defaultMessage : "Une erreur est survenue";
        }
    }
}
