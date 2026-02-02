<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>MasterAnnonce - Ajouter une annonce</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>

    <body>
        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6">
                    <div class="card shadow">
                        <div class="card-header bg-primary text-white">
                            <h2 class="mb-0">Nouvelle Annonce</h2>
                        </div>
                        <div class="card-body">
                            <form action="AnnonceAdd" method="post">
                                <div class="mb-3">
                                    <label for="title" class="form-label">Titre <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="title" name="title" maxlength="64"
                                        required placeholder="Ex: Vends vélo de ville">
                                </div>

                                <div class="mb-3">
                                    <label for="description" class="form-label">Description <span
                                            class="text-danger">*</span></label>
                                    <textarea class="form-control" id="description" name="description" rows="4"
                                        maxlength="256" required
                                        placeholder="Décrivez votre annonce en détail..."></textarea>
                                    <div class="form-text">Maximum 256 caractères</div>
                                </div>

                                <div class="mb-3">
                                    <label for="adress" class="form-label">Adresse <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="adress" name="adress" maxlength="64"
                                        required placeholder="Ex: Paris, 75001">
                                </div>

                                <div class="mb-3">
                                    <label for="mail" class="form-label">Email <span
                                            class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="mail" name="mail" maxlength="64"
                                        required placeholder="votre.email@exemple.com">
                                </div>

                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <a href="AnnonceList" class="btn btn-secondary">Annuler</a>
                                    <button type="submit" class="btn btn-primary">Enregistrer</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>

    </html>