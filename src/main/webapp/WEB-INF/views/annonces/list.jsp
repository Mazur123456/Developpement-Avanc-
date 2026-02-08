<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Annonces - MasterAnnonce</title>
                <style>
                    * {
                        box-sizing: border-box;
                        margin: 0;
                        padding: 0;
                    }

                    body {
                        font-family: Arial, sans-serif;
                        background: #f5f5f5;
                    }

                    nav {
                        background: #343a40;
                        color: white;
                        padding: 1rem 2rem;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    nav a {
                        color: white;
                        text-decoration: none;
                        margin-left: 1rem;
                    }

                    nav a:hover {
                        text-decoration: underline;
                    }

                    .container {
                        max-width: 1200px;
                        margin: 2rem auto;
                        padding: 0 1rem;
                    }

                    h1 {
                        margin-bottom: 1.5rem;
                    }

                    .annonces {
                        display: grid;
                        gap: 1rem;
                    }

                    .annonce-card {
                        background: white;
                        padding: 1.5rem;
                        border-radius: 8px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                    }

                    .annonce-card h2 {
                        margin-bottom: 0.5rem;
                        color: #333;
                    }

                    .annonce-card p {
                        color: #666;
                        margin-bottom: 0.5rem;
                    }

                    .annonce-card .meta {
                        font-size: 0.9rem;
                        color: #888;
                    }

                    .annonce-card .status {
                        display: inline-block;
                        padding: 0.25rem 0.5rem;
                        border-radius: 4px;
                        font-size: 0.8rem;
                    }

                    .status-DRAFT {
                        background: #ffc107;
                        color: #000;
                    }

                    .status-PUBLISHED {
                        background: #28a745;
                        color: #fff;
                    }

                    .status-ARCHIVED {
                        background: #6c757d;
                        color: #fff;
                    }

                    .btn {
                        display: inline-block;
                        padding: 0.5rem 1rem;
                        border-radius: 4px;
                        text-decoration: none;
                        font-size: 0.9rem;
                    }

                    .btn-primary {
                        background: #007bff;
                        color: white;
                    }

                    .btn-success {
                        background: #28a745;
                        color: white;
                    }

                    .pagination {
                        margin-top: 2rem;
                        text-align: center;
                    }

                    .pagination a {
                        padding: 0.5rem 1rem;
                        margin: 0 0.25rem;
                        background: white;
                        text-decoration: none;
                        border-radius: 4px;
                    }

                    .pagination a.active {
                        background: #007bff;
                        color: white;
                    }

                    .empty {
                        text-align: center;
                        padding: 3rem;
                        color: #666;
                    }
                </style>
            </head>

            <body>
                <nav>
                    <div><strong>MasterAnnonce</strong></div>
                    <div>
                        <a href="${pageContext.request.contextPath}/annonces">Annonces</a>
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/annonce/create">+ Créer</a>
                                <span>| ${sessionScope.username}</span>
                                <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login">Connexion</a>
                                <a href="${pageContext.request.contextPath}/register">Inscription</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </nav>

                <div class="container">
                    <h1>Annonces</h1>

                    <c:if test="${empty annonces}">
                        <div class="empty">
                            <p>Aucune annonce pour le moment.</p>
                            <c:if test="${not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/annonce/create"
                                    class="btn btn-success">Créer une annonce</a>
                            </c:if>
                        </div>
                    </c:if>

                    <div class="annonces">
                        <c:forEach var="annonce" items="${annonces}">
                            <div class="annonce-card">
                                <h2><a
                                        href="${pageContext.request.contextPath}/annonce/detail?id=${annonce.id}">${annonce.title}</a>
                                </h2>
                                <p>${annonce.description}</p>
                                <div class="meta">
                                    <span class="status status-${annonce.status}">${annonce.status}</span>
                                    <c:if test="${not empty annonce.category}">
                                        | Catégorie: ${annonce.category.label}
                                    </c:if>
                                    |
                                    <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" />
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="${pageContext.request.contextPath}/annonces?page=${i}"
                                    class="${currentPage == i ? 'active' : ''}">${i}</a>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
            </body>

            </html>