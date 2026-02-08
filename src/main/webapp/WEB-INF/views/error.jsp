<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <title>Erreur ${statusCode} - MasterAnnonce</title>
            <style>
                * {
                    box-sizing: border-box;
                    margin: 0;
                    padding: 0;
                }

                body {
                    font-family: Arial, sans-serif;
                    background: #f5f5f5;
                    min-height: 100vh;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .error-container {
                    background: white;
                    padding: 3rem;
                    border-radius: 8px;
                    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
                    text-align: center;
                    max-width: 500px;
                }

                .error-code {
                    font-size: 6rem;
                    font-weight: bold;
                    color: #dc3545;
                    line-height: 1;
                }

                .error-message {
                    font-size: 1.5rem;
                    color: #333;
                    margin: 1rem 0;
                }

                .error-details {
                    color: #666;
                    margin-bottom: 2rem;
                }

                .btn {
                    display: inline-block;
                    padding: 0.75rem 1.5rem;
                    background: #007bff;
                    color: white;
                    text-decoration: none;
                    border-radius: 4px;
                }

                .btn:hover {
                    background: #0056b3;
                }

                .back-link {
                    margin-top: 1rem;
                }

                .back-link a {
                    color: #007bff;
                    text-decoration: none;
                }
            </style>
        </head>

        <body>
            <div class="error-container">
                <div class="error-code">${statusCode}</div>
                <div class="error-message">${errorMessage}</div>

                <c:if test="${not empty requestUri}">
                    <div class="error-details">
                        URL demandée: ${requestUri}
                    </div>
                </c:if>

                <c:if test="${not empty exceptionMessage}">
                    <div class="error-details">
                        Détails: ${exceptionMessage}
                    </div>
                </c:if>

                <a href="${pageContext.request.contextPath}/annonces" class="btn">Retour à l'accueil</a>

                <div class="back-link">
                    <a href="javascript:history.back()">← Retour à la page précédente</a>
                </div>
            </div>
        </body>

        </html>