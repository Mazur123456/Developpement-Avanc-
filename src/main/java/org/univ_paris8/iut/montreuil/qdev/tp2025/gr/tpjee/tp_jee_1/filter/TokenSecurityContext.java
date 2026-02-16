package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class TokenSecurityContext implements SecurityContext {

    private final User user;
    private final String scheme;

    public TokenSecurityContext(User user, String scheme) {
        this.user = user;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> user.getUsername();
    }

    @Override
    public boolean isUserInRole(String role) {
        // Pour l'instant on ne gère pas les rôles
        return true;
    }

    @Override
    public boolean isSecure() {
        return "https".equalsIgnoreCase(scheme);
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH; // Ou "Bearer"
    }

    public User getUser() {
        return user;
    }
}
