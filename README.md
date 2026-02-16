# MasterAnnonce - API Backend (TP3)

Application Java EE modernisée, évoluant d'une application Servlet/JSP (TP2) vers une **API REST JAX-RS sécurisée et stateless** (TP3), toujours basée sur JPA/Hibernate et une architecture en couches.

## 🎓 Contexte Pédagogique (TP3)

Ce projet répond aux exigences du TP "Dev Avancé #3".
**Objectif** : Transformer `MasterAnnonce` en un backend API professionnel.

- **Stack** : Java EE / Jakarta EE pur (Pas de Spring).
- **Communication** : JSON uniquement.
- **Sécurité** : Stateless (Token-based).
- **Qualité** : Tests unitaires & intégration, Architecture en couches.

## 📦 Technologies Utilisées

- **Java 11** + **Maven 3+**
- **JAX-RS (Jersey 2.x)** : Framework REST pour l'exposition des ressources.
    - *Choix de configuration* : Utilisation de `ResourceConfig` (Jersey) plutôt que `Application` standard pour une configuration plus flexible des packages et des filtres.
- **Jackson** : Sérialisation/Désérialisation JSON.
- **JPA 2.2** (javax.persistence) + **Hibernate ORM 5.6** : Persistance.
- **PostgreSQL** : Base de données relationnelle (Driver JDBC 42.7).
- **Bean Validation** (Hibernate Validator 6.2) : Validation des DTOs.
- **JUnit 5 + Mockito** : Tests unitaires et d'intégration.
- **Bootstrap 5.3.0** : (Héritage TP2) Pour les pages webs résiduelles.

## 🏗️ Architecture Technique

Architecture en couches stricte respectant le principe de séparation des responsabilités :

```
src/main/java/org/univ_paris8/iut/montreuil/qdev/tp2025/gr/tpjee/tp_jee_1/
├── entity/         # Entités JPA (User, Category, Annonce) - Modèle de données
├── dto/            # DTOs (Data Transfer Objects) - Contrats d'interface API
├── repository/     # Variable d'accès aux données (JPQL pur, Stateless, sans transaction)
├── service/        # Logique métier & Gestion des Transactions (ACID)
├── resource/       # Contrôleurs JAX-RS (Endpoints HTTP, validation entrées, retour JSON)
├── filter/         # Filtres JAX-RS (Sécurité AuthFilter, CORS, etc.)
├── exception/      # Gestion centralisée des erreurs (GlobalExceptionMapper)
└── utils/          # Utilitaires (JPAUtil pour l'EMF, PasswordUtil pour le hachage)
```

### Justification de l'Architecture
*   **Resource vs Service** : Les contrôleurs JAX-RS (`Resource`) ne contiennent *aucune* logique métier. Ils ne font que déléguer au `Service` et gérer le protocole HTTP (Codes 200, 400, 404...).
*   **Service vs Repository** : Les Services gèrent les transactions (`begin`/`commit`). Les Repositories sont passifs et reçoivent l'`EntityManager` injecté par le Service.

## 🔐 Authentification & Sécurité (Exercice 5 & 6)

L'application implémente une sécurité **Stateless** (sans session serveur `HttpSession`) basée sur des **Tokens**.

### Flow d'Authentification
1.  **Login** (`POST /api/login`) :
    *   Le client envoie `{"username": "...", "password": "..."}`.
    *   Le serveur vérifie les identifiants hashés (SHA-256).
    *   Si OK, le serveur génère un **Token Unique** (UUID) stocké temporairement en mémoire (`AuthService`).
    *   Le serveur retourne `{"token": "..."}`.
2.  **Accès Sécurisé** :
    *   Pour accéder aux routes protégées (ex: `POST /api/annonces`), le client **doit** envoyer le header : `Authorization: Bearer <token>`.
3.  **Filtrage** (`AuthFilter`) :
    *   Intercepte chaque requête API.
    *   Vérifie la présence et la validité du token.
    *   Si valide : Reconstitue l'identité utilisateur (`SecurityContext`) pour la requête en cours.
    *   Si invalide : Retourne immédiatement `401 Unauthorized`.

## 🚀 Installation et Démarrage

### Prérequis
- Java 11+
- Maven 3+
- Docker & Docker Compose

### Lancement avec Docker ("Tout en un")
Cette commande lance **PostgreSQL** (Base de données) et **Tomcat** (Serveur d'Application) configurés automatiquement.

1. **Nettoyage (Optionnel)** :
   ```bash
   docker-compose down
   ```
2. **Compilation (Obligatoire)** : Le Dockerfile copiant le .war, il faut le générer avant.
   ```bash
   ./mvnw clean package
   ```
3. **Lancement** :
   ```bash
   docker-compose up --build
   ```

> **🏠 Application Web** : `http://localhost:8080/MasterAnnonce/` (Ou `index.jsp`)
> **🔌 Accès API** : `http://localhost:8080/MasterAnnonce/api/annonces`
> **🔑 Login** : `POST http://localhost:8080/MasterAnnonce/api/login`

---

## 📡 Endpoints API Principaux

| Verbe | URI | Description | Auth |
|-------|-----|-------------|------|
| **POST** | `/MasterAnnonce/api/login` | Récupération du token | ❌ Non |
| **GET** | `/MasterAnnonce/api/annonces` | Liste paginée des annonces | ❌ Non |
| **GET** | `/MasterAnnonce/api/annonces/{id}` | Détail d'une annonce | ❌ Non |
| **POST** | `/MasterAnnonce/api/annonces` | Créer une annonce | ✅ Token |
| **PUT** | `/MasterAnnonce/api/annonces/{id}` | Modifier (Auteur uniquement) | ✅ Token |
| **DELETE**| `/MasterAnnonce/api/annonces/{id}` | Supprimer (Auteur uniquement)| ✅ Token |

## 🧪 Tests & Qualité (Exercice 10)

L'application dispose de deux types de tests séparés :

1.  **Tests Unitaires (Unit)** : Testent la logique métier isolée (Services) et les Utilitaires. Rapides, pas de BDD.
2.  **Tests d'Intégration (IT)** : Testent la persistance (Repositories) et les scénarios complets avec une base de données H2 en mémoire.

**Pourquoi séparer ?**
*   Les tests unitaires sont exécutés à chaque build (feedback immédiat).
*   Les tests d'intégration sont plus lents et peuvent être exécutés moins souvent (CI/CD).

## 🐛 Problèmes Rencontrés & Solutions

### 1. LazyInitializationException
*   **Problème** : Accès aux collections/entités liées (`annonce.getAuthor()`) après la fermeture de la transaction/EntityManager lors de la sérialisation JSON.
*   **Solution** : Utilisation systématique de requêtes **`JOIN FETCH`** dans les repositories pour charger les données nécessaires en une seule fois.

### 2. Conflit Servlet / JAX-RS
*   **Problème** : Les anciens Servlets (TP2) captaient certaines URL ou entraient en conflit avec le mapping `/api/*`.
*   **Solution** : Configuration précise dans `web.xml` pour isoler `JerseyServlet` sur `/api/*` et laisser les anciens Servlets sur `/`.

### 3. Sécurité Stateless sans Session
*   **Problème** : Comment sécuriser l'API sans utiliser le mécanisme `HttpSession` de Java EE ?
*   **Solution** : Implémentation manuelle d'un système de Token (Map en mémoire) et surcharge du `SecurityContext` de JAX-RS pour injecter l'utilisateur courant dans les Ressources.

### 4. Configuration Docker vs Local
*   **Problème** : Hardcoder les accès BDD dans `persistence.xml` casse le déploiement sur différents environnements.
*   **Solution** : Utilisation d'une classe utilitaire `JPAUtil` qui lit les **variables d'environnement** (`DB_HOST`, `DB_PORT`) pour surcharger la configuration à la volée.

## 📄 Licence
Projet universitaire - BUT 3 S6.R5 - Développement Avancé
