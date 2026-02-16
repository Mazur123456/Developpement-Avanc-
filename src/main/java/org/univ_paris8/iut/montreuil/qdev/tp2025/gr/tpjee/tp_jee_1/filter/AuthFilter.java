package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AuthService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Optional;

/**
 * Filtre JAX-RS pour sécuriser l'API via Token (Bearer).
 * Remplace l'ancien filtre basé sur les Sessions Servlet.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private final AuthService authService;

    public AuthFilter() {
        this.authService = new AuthService();
    }

    // Pour les tests
    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // URLs publiques (ne nécessitant pas de token)
        String path = requestContext.getUriInfo().getPath();
        if (isPublicPath(path, requestContext.getMethod())) {
            return;
        }

        // Récupérer le header Authorization
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Valider le header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extraire le token
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        // Valider le token
        Optional<User> userOpt = authService.validateToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Créer un SecurityContext custom pour injecter l'utilisateur
            TokenSecurityContext securityContext = new TokenSecurityContext(user,
                    requestContext.getUriInfo().getRequestUri().getScheme());
            requestContext.setSecurityContext(securityContext);
        } else {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isPublicPath(String path, String method) {
        // Liste des endpoints publics
        return path.equals("auth/login") ||
                path.equals("login") ||
                path.equals("helloWorld") ||
                (path.startsWith("annonces") && "GET".equalsIgnoreCase(method));
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                        .entity("{\"error\": \"Authentication required\"}")
                        .build());
    }
}
