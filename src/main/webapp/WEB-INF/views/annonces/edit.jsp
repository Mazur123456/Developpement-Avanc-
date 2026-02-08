<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <title>Modifier - ${annonce.title}</title>
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
                    max-width: 600px;
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
                    margin-bottom: 1.5rem;
                }

                .form-group {
                    margin-bottom: 1rem;
                }

                label {
                    display: block;
                    margin-bottom: 0.5rem;
                    font-weight: bold;
                }

                input,
                textarea,
                select {
                    width: 100%;
                    padding: 0.75rem;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    font-size: 1rem;
                }

                textarea {
                    resize: vertical;
                    min-height: 100px;
                }

                button {
                    padding: 0.75rem 1.5rem;
                    background: #007bff;
                    color: white;
                    border: none;
                    border-radius: 4px;
                    font-size: 1rem;
                    cursor: pointer;
                }

                button:hover {
                    background: #0056b3;
                }

                .error {
                    background: #f8d7da;
                    color: #721c24;
                    padding: 0.75rem;
                    border-radius: 4px;
                    margin-bottom: 1rem;
                }

                .back {
                    margin-bottom: 1rem;
                }

                .back a {
                    color: #007bff;
                    text-decoration: none;
                }

                .field-error {
                    color: #dc3545;
                    font-size: 0.875rem;
                    margin-top: 0.25rem;
                }
            </style>
        </head>

        <body>
            <nav>
                <div><strong>MasterAnnonce</strong></div>
                <div>
                    <a href="${pageContext.request.contextPath}/annonces">Annonces</a>
                    <span>| ${sessionScope.username}</span>
                    <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
                </div>
            </nav>

            <div class="container">
                <div class="back">
                    <a href="${pageContext.request.contextPath}/annonce/detail?id=${annonce.id}">← Retour au détail</a>
                </div>

                <div class="card">
                    <h1>Modifier l'annonce</h1>

                    <c:if test="${not empty error}">
                        <div class="error">${error}</div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/annonce/edit">
                        <input type="hidden" name="id" value="${annonce.id}">

                        <div class="form-group">
                            <label for="title">Titre *</label>
                            <input type="text" id="title" name="title" value="${annonce.title}" required maxlength="64">
                            <c:if test="${not empty errors['title']}">
                                <div class="field-error">${errors['title']}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description" name="description"
                                maxlength="256">${annonce.description}</textarea>
                            <c:if test="${not empty errors['description']}">
                                <div class="field-error">${errors['description']}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="categoryId">Catégorie</label>
                            <select id="categoryId" name="categoryId">
                                <option value="">-- Sélectionner --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}" ${annonce.category !=null && annonce.category.id==cat.id
                                        ? 'selected' : '' }>${cat.label}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="adress">Adresse</label>
                            <input type="text" id="adress" name="adress" value="${annonce.adress}" maxlength="64">
                            <c:if test="${not empty errors['adress']}">
                                <div class="field-error">${errors['adress']}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="mail">Email de contact</label>
                            <input type="email" id="mail" name="mail" value="${annonce.mail}" maxlength="64">
                            <c:if test="${not empty errors['mail']}">
                                <div class="field-error">${errors['mail']}</div>
                            </c:if>
                        </div>
                        <button type="submit">Enregistrer les modifications</button>
                    </form>
                </div>
            </div>
        </body>

        </html>