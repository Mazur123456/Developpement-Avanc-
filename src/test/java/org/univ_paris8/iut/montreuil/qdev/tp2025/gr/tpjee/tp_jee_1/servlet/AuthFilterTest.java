package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter.AuthFilter;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AuthService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private AuthService authService; // On ne peut pas facilement mocker le service instancié en interne sans
                                     // Refactoring (Injection de dépendance)
    // Problème : AuthFilter instancie AuthService avec "new AuthService()".
    // Solution pour le test :
    // 1. Soit on modifie AuthFilter pour permettre l'injection (setter ou
    // constructeur).
    // 2. Soit on teste avec le vrai AuthService (mais besoin de fausse BDD ?).
    // 3. Soit on utilise PowerMock (lourd).

    // Pour ce TP sans framework d'injection (CDI/Spring), le "new" est
    // problématique pour les tests unitaires isolés.
    // On va modifier AuthFilter pour avoir un constructeur par défaut (pour JAX-RS)
    // et un constructeur avec Service (pour les tests).

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private UriInfo uriInfo;

    @BeforeEach
    void setUp() throws URISyntaxException {
        // Setup commun
        lenient().when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    @Test
    void testPublicUrlPasses() throws IOException, URISyntaxException {
        when(uriInfo.getPath()).thenReturn("login");

        authFilter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(requestContext, never()).setSecurityContext(any());
    }

    @Test
    void testProtectedUrlWithoutHeaderFails() throws IOException, URISyntaxException {
        when(uriInfo.getPath()).thenReturn("annonces"); // GET est public ? Ah non, dans mon code `isGetRequest`
                                                        // retournait false hardcodé par sécurité
        // Disons une URL clairement protégée
        when(uriInfo.getPath()).thenReturn("secure/resource");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authFilter.filter(requestContext);

        verify(requestContext).abortWith(any());
    }

    @Test
    void testProtectedUrlWithBadHeaderFails() throws IOException, URISyntaxException {
        when(uriInfo.getPath()).thenReturn("secure/resource");
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Basic xyz");

        authFilter.filter(requestContext);

        verify(requestContext).abortWith(any());
    }

    // Note: Pour tester le cas PASSANT (token valide), il faut que je puisse mocker
    // AuthService.
    // Je vais devoir modifier AuthFilter pour ajouter un point d'injection.
}
