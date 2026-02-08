<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>${annonce.title} - MasterAnnonce</title>
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

                    .container {
                        max-width: 800px;
                        margin: 2rem auto;
                        padding: 0 1rem;
                    }

                    .card {
                        background: white;
                        padding: 2rem;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    }

                    h1 {
                        margin-bottom: 1rem;
                    }

                    .meta {
                        color: #666;
                        margin-bottom: 1.5rem;
                    }

                    .status {
                        display: inline-block;
                        padding: 0.25rem 0.5rem;
                        border-radius: 4px;
                        font-size: 0.9rem;
                        margin-right: 1rem;
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

                    .description {
                        margin-bottom: 1.5rem;
                        line-height: 1.6;
                    }

                    .info {
                        background: #f8f9fa;
                        padding: 1rem;
                        border-radius: 4px;
                        margin-bottom: 1.5rem;
                    }

                    .info p {
                        margin-bottom: 0.5rem;
                    }

                    .actions {
                        display: flex;
                        gap: 0.5rem;
                        flex-wrap: wrap;
                    }

                    .btn {
                        padding: 0.5rem 1rem;
                        border-radius: 4px;
                        text-decoration: none;
                        border: none;
                        cursor: pointer;
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

                    .btn-warning {
                        background: #ffc107;
                        color: #000;
                    }

                    .btn-secondary {
                        background: #6c757d;
                        color: white;
                    }

                    .back {
                        margin-bottom: 1rem;
                    }

                    .back a {
                        color: #007bff;
                        text-decoration: none;
                    }
                </style>
            </head>

            <body>
                <nav>
                    <div><strong>MasterAnnonce</strong></div>
                    <div>
                        <a href="${pageContext.request.contextPath}/annonces">Annonces</a>
                        <c:if test="${not empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/annonce/create">+ Créer</a>
                            <span>| ${sessionScope.username}</span>
                            <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
                        </c:if>
                    </div>
                </nav>

                <div class="container">
                    <div class="back">
                        <a href="${pageContext.request.contextPath}/annonces">← Retour aux annonces</a>
                    </div>

                    <div class="card">
                        <h1>${annonce.title}</h1>

                        <div class="meta">
                            <span class="status status-${annonce.status}">${annonce.status}</span>
                            <c:if test="${not empty annonce.category}">
                                Catégorie: <strong>${annonce.category.label}</strong>
                            </c:if>
                            | Publié le
                            <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy à HH:mm" />
                            <c:if test="${not empty annonce.author}">
                                | Par <strong>${annonce.author.username}</strong>
                            </c:if>
                        </div>

                        <div class="description">
                            <p>${annonce.description}</p>
                        </div>

                        <div class="info">
                            <p><strong>Adresse:</strong> ${annonce.adress}</p>
                            <p><strong>Contact:</strong> ${annonce.mail}</p>
                        </div>

                        <c:if test="${not empty sessionScope.user}">
                            <div class="actions">
                                <a href="${pageContext.request.contextPath}/annonce/edit?id=${annonce.id}"
                                    class="btn btn-primary">Modifier</a>

                                <c:if test="${annonce.status == 'DRAFT'}">
                                    <form action="${pageContext.request.contextPath}/annonce/publish" method="post"
                                        style="display:inline;">
                                        <input type="hidden" name="id" value="${annonce.id}">
                                        <button type="submit" class="btn btn-success">Publier</button>
                                    </form>
                                </c:if>

                                <c:if test="${annonce.status == 'PUBLISHED'}">
                                    <form action="${pageContext.request.contextPath}/annonce/archive" method="post"
                                        style="display:inline;">
                                        <input type="hidden" name="id" value="${annonce.id}">
                                        <button type="submit" class="btn btn-warning">Archiver</button>
                                    </form>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </body>

            </html>