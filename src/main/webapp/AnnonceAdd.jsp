<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Ajouter une annonce</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body class="container">
<h1>Nouvelle Annonce</h1>
<form action="AnnonceAdd" method="post" class="well">
    <div class="form-group">
        <label>Titre :</label>
        <input type="text" name="title" class="form-control" required>
    </div>
    <div class="form-group">
        <label>Description :</label>
        <textarea name="description" class="form-control" required></textarea>
    </div>
    <div class="form-group">
        <label>Adresse :</label>
        <input type="text" name="adress" class="form-control" required>
    </div>
    <div class="form-group">
        <label>Email :</label>
        <input type="email" name="mail" class="form-control" required>
    </div>
    <button type="submit" class="btn btn-success">Enregistrer</button>
</form>
</body>
</html>