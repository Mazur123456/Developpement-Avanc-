package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter.AuthFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 4 – Tests Web : filtre d'authentification
 * Utilise des mocks HTTP simples (pas de framework)
 */
class AuthFilterTest {

    private AuthFilter filter;

    @BeforeEach
    void setUp() throws ServletException {
        filter = new AuthFilter();
        // Initialiser le filtre avec un FilterConfig minimal
        filter.init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return "AuthFilter";
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.emptyEnumeration();
            }
        });
    }

    @Test
    @DisplayName("Les URLs publiques sont accessibles sans authentification")
    void testPublicUrlsAccessible() {
        // /login est une URL publique, le filtre doit laisser passer
        String[] publicUrls = { "/login", "/register", "/annonces" };

        for (String url : publicUrls) {
            MockHttpServletRequest request = new MockHttpServletRequest(url);
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            assertDoesNotThrow(() -> filter.doFilter(request, response, chain),
                    "Le filtre ne doit pas bloquer " + url);
            assertTrue(chain.wasChainCalled(), "La chaîne doit être appelée pour " + url);
        }
    }

    @Test
    @DisplayName("Les ressources statiques sont accessibles sans authentification")
    void testStaticResourcesAccessible() {
        String[] staticUrls = { "/css/style.css", "/js/app.js", "/images/logo.png" };

        for (String url : staticUrls) {
            MockHttpServletRequest request = new MockHttpServletRequest(url);
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
            assertTrue(chain.wasChainCalled(), "Les ressources statiques doivent passer pour " + url);
        }
    }

    @Test
    @DisplayName("Les URLs protégées redirigent vers login sans session")
    void testProtectedUrlsRedirectWithoutSession() {
        MockHttpServletRequest request = new MockHttpServletRequest("/annonce/create");
        request.setSession(null); // Pas de session
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
        assertFalse(chain.wasChainCalled(), "La chaîne NE doit PAS être appelée");
        assertTrue(response.getRedirectUrl().contains("login"),
                "Doit rediriger vers login");
    }

    @Test
    @DisplayName("Les URLs protégées sont accessibles avec un utilisateur en session")
    void testProtectedUrlsAccessibleWithSession() {
        MockHttpServletRequest request = new MockHttpServletRequest("/annonce/create");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", "testUser"); // Utilisateur connecté
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
        assertTrue(chain.wasChainCalled(), "La chaîne doit être appelée pour un utilisateur connecté");
    }

    // ==================== Mock HTTP Classes ====================

    /**
     * Mock simple de HttpServletRequest
     */
    static class MockHttpServletRequest implements HttpServletRequest {
        private final String requestURI;
        private HttpSession session;
        private final String contextPath = "";

        MockHttpServletRequest(String requestURI) {
            this.requestURI = requestURI;
        }

        void setSession(HttpSession session) {
            this.session = session;
        }

        @Override
        public String getRequestURI() {
            return requestURI;
        }

        @Override
        public String getContextPath() {
            return contextPath;
        }

        @Override
        public HttpSession getSession(boolean create) {
            if (create && session == null)
                session = new MockHttpSession();
            return session;
        }

        @Override
        public HttpSession getSession() {
            return getSession(true);
        }

        // Méthodes non utilisées
        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return "GET";
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(requestURI);
        }

        @Override
        public String getServletPath() {
            return requestURI;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(String username, String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public Collection<Part> getParts() {
            return null;
        }

        @Override
        public Part getPart(String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public java.io.BufferedReader getReader() {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest req, ServletResponse res) {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }
    }

    /**
     * Mock simple de HttpServletResponse
     */
    static class MockHttpServletResponse implements HttpServletResponse {
        private String redirectUrl;
        private int status = 200;

        String getRedirectUrl() {
            return redirectUrl;
        }

        @Override
        public void sendRedirect(String location) {
            this.redirectUrl = location;
        }

        @Override
        public void setStatus(int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        // Méthodes non utilisées
        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return url;
        }

        @Override
        public String encodeUrl(String url) {
            return url;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return url;
        }

        @Override
        public void sendError(int sc, String msg) {
        }

        @Override
        public void sendError(int sc) {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc, String sm) {
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return null;
        }

        @Override
        public java.io.PrintWriter getWriter() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void setContentType(String type) {
        }

        @Override
        public void setBufferSize(int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(Locale loc) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }
    }

    /**
     * Mock simple de FilterChain pour vérifier si doFilter a été appelé
     */
    static class MockFilterChain implements FilterChain {
        private boolean chainCalled = false;

        @Override
        public void doFilter(ServletRequest req, ServletResponse res) {
            this.chainCalled = true;
        }

        boolean wasChainCalled() {
            return chainCalled;
        }
    }

    /**
     * Mock simple de HttpSession
     */
    static class MockHttpSession implements HttpSession {
        private final Map<String, Object> attributes = new HashMap<>();

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public void setAttribute(String name, Object value) {
            attributes.put(name, value);
        }

        @Override
        public void removeAttribute(String name) {
            attributes.remove(name);
        }

        // Méthodes non utilisées
        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public String getId() {
            return "mock-session";
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }

        @Override
        public HttpSessionContext getSessionContext() {
            return null;
        }

        @Override
        public Object getValue(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getValueNames() {
            return new String[0];
        }

        @Override
        public void putValue(String name, Object value) {
        }

        @Override
        public void removeValue(String name) {
        }

        @Override
        public void invalidate() {
        }

        @Override
        public boolean isNew() {
            return false;
        }
    }
}
