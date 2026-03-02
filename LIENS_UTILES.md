# 🛠️ Liens Utiles et Outils

Voici le récapitulatif de tous les liens importants pour tester et interagir avec l'application, ainsi que les identifiants de test.

## 🔗 Liens de l'Application

* **Racine de l'Application / API :**
  [`http://localhost:8080/api`](http://localhost:8080/api)
  *(Sert de base à toutes les requêtes REST)*

* **Interface Swagger UI (Documentation OpenAPI) :**
  [`http://localhost:8080/api/swagger-ui/index.html`](http://localhost:8080/api/swagger-ui/index.html)
  *(Permet de visualiser et tester l'API visuellement)*

* **Documentation OpenAPI (Format JSON) :**
  [`http://localhost:8080/api/v3/api-docs`](http://localhost:8080/api/v3/api-docs)

* **Healthcheck & Actuator :**
  [`http://localhost:8080/api/actuator/health`](http://localhost:8080/api/actuator/health)
  [`http://localhost:8080/api/actuator/info`](http://localhost:8080/api/actuator/info)

---

## 🔐 Identifiants de Test (Déjà existant en base)

Un compte Administrateur est automatiquement créé au démarrage de l'application (via `DataInitializer`) si la base de données est vide.

* **URL de Connexion :** `POST http://localhost:8080/api/auth/login`
* **Format du Body (JSON) :**
  ```json
  {
    "email": "admin@test.com",
    "password": "password"
  }
  ```

---

## 🚀 Exemples de Requêtes (cURL)

**1. Récupérer le Token JWT (Login)**
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d "{\"email\":\"admin@test.com\",\"password\":\"password\"}"
```

**2. Utiliser le Token pour lire les annonces**
*(Remplacez `<VOTRE_TOKEN>` par le token obtenu à l'étape précédente)*
```bash
curl -X GET http://localhost:8080/api/annonces \
     -H "Authorization: Bearer <VOTRE_TOKEN>"
```
