package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre de sécurité pour protéger les ressources authentifiées
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // URLs publiques (pas besoin d'authentification)
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/login",
            "/register",
            "/logout",
            "/annonces",
            "/annonce/detail");

    // Extensions de ressources statiques
    private static final List<String> STATIC_EXTENSIONS = Arrays.asList(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Permettre l'accès aux ressources statiques
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Permettre l'accès aux URLs publiques
        if (isPublicUrl(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Vérifier l'authentification pour les autres URLs
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Sauvegarder l'URL demandée pour redirection après login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectUrl", requestURI);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Utilisateur authentifié, continuer
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }

    private boolean isPublicUrl(String path) {
        if (path.equals("/") || path.isEmpty()) {
            return true;
        }
        for (String publicUrl : PUBLIC_URLS) {
            if (path.startsWith(publicUrl)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStaticResource(String path) {
        for (String ext : STATIC_EXTENSIONS) {
            if (path.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
