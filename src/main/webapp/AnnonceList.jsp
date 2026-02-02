<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MasterAnnonce - Liste des annonces</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row mb-4">
            <div class="col">
                <h1 class="display-4">MasterAnnonce</h1>
                <p class="lead">Annonces disponibles</p>
            </div>
            <div class="col-auto">
                <a href="AnnonceAdd" class="btn btn-primary btn-lg">
                    <i class="bi bi-plus-circle"></i> Ajouter une annonce
                </a>
            </div>
        </div>
        
        <hr>
        
        <c:if test="${empty annonces}">
            <div class="alert alert-info" role="alert">
                <h4 class="alert-heading">Aucune annonce disponible</h4>
                <p>Il n'y a pas d'annonces pour le moment. Soyez le premier à en publier une !</p>
            </div>
        </c:if>
        
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach var="annonce" items="${annonces}">
                <div class="col">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title"><c:out value="${annonce.title}"/></h5>
                            <p class="card-text"><c:out value="${annonce.description}"/></p>
                        </div>
                        <div class="card-footer bg-transparent">
                            <p class="mb-2">
                                <strong>Contact:</strong> <c:out value="${annonce.mail}"/><br>
                                <strong>Lieu:</strong> <c:out value="${annonce.adress}"/>
                            </p>
                            <div class="d-flex gap-2">
                                <a href="AnnonceUpdate?id=${annonce.id}" class="btn btn-sm btn-outline-primary flex-fill">
                                    Modifier
                                </a>
                                <a href="AnnonceDelete?id=${annonce.id}" 
                                   class="btn btn-sm btn-outline-danger flex-fill"
                                   onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?');">
                                    Supprimer
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
