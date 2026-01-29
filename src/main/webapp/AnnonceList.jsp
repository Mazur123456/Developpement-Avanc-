<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Liste des annonces</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body class="container">
<h1>Annonces disponibles</h1>
<a href="AnnonceAdd" class="btn btn-primary">Ajouter une annonce</a>
<hr>
<div class="list-group">
    <c:forEach var="item" items="${annonces}">
        <div class="list-group-item">
            <h4 class="list-group-item-heading">${item.title}</h4>
            <p class="list-group-item-text">${item.description}</p>
            <small>Contact : ${item.mail} | Lieu : ${item.adress}</small>
        </div>
    </c:forEach>
</div>
</body>
</html>

