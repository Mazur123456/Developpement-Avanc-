# MasterAnnonce API — TP Développement Avancé #4

> **Migration vers Spring Boot 3 – Sécurité JWT, Documentation, Tests & Industrialisation**
>
> Projet universitaire BUT 3 — S6.R5 — Développement Avancé

---

## 📋 Sommaire

1. [Architecture Technique](#-architecture-technique)
2. [Stack Technologique](#-stack-technologique)
3. [Démarrage rapide](#-démarrage-rapide)
4. [Endpoints API](#-endpoints-api)
5. [Authentification JWT](#-authentification-jwt)
6. [Composants Clés](#-composants-clés)
7. [Tests](#-tests)
8. [Documentation OpenAPI](#-documentation-openapi--swagger)
9. [Monitoring Actuator](#-monitoring-actuator)
10. [Docker & Déploiement](#-docker--déploiement)
11. [CI/CD — GitHub Actions](#-cicd--github-actions)
12. [Problèmes Rencontrés & Solutions](#-problèmes-rencontrés--solutions)

---

## 🏗️ Architecture Technique

Architecture en couches strict (**Partie I, Ex. 2**) :

```
src/main/java/org/.../tp_jee_1/
│
├── controller/       # @RestController  — Reçoit les requêtes HTTP, délègue aux services
│   ├── AnnonceRestController.java
│   ├── AuthRestController.java
│   ├── CategoryRestController.java
│   ├── MetaRestController.java
│   └── CustomErrorController.java
│
├── service/          # Logique métier, règles d'accès, transactions (@Transactional)
│   ├── AnnonceService.java
│   ├── CategoryService.java
│   ├── UserService.java
│   └── CustomUserDetailsService.java
│
├── repository/       # Spring Data JPA — pas de JPQL géante
│   ├── AnnonceRepository.java       (JpaSpecificationExecutor)
│   ├── AnnonceSpecifications.java   (recherche dynamique)
│   ├── CategoryRepository.java
│   └── UserRepository.java
│
├── dto/              # Contrat API — jamais les entités exposées directement
│   ├── AnnonceDTO.java / AnnonceCreateDTO.java / AnnonceUpdateDTO.java
│   ├── AnnonceFilterDTO.java
│   ├── CategoryDTO.java
│   ├── LoginDTO.java / TokenDTO.java
│
├── mapper/           # MapStruct — conversions automatiques entité ↔ DTO
│   ├── AnnonceMapper.java
│   └── CategoryMapper.java
│
├── entity/           # Modèle JPA (Annonce, User, Category, AnnonceStatus)
├── security/         # JWT: JwtUtil, JwtAuthenticationFilter, CustomUserDetails(Service)
├── aspect/           # Spring AOP: LoggingAspect (durée, erreurs, correlationId)
├── filter/           # OncePerRequestFilter: CorrelationIdFilter (MDC)
├── config/           # SecurityConfig, OpenApiConfig, DataInitializer
└── exception/        # GlobalExceptionHandler, EntityNotFoundException, etc.
```

---

## 💻 Stack Technologique

| Technologie | Version | Usage |
|--|--|--|
| Java | 17 | Runtime principal |
| Spring Boot | 3.2.5 | Framework général |
| Spring Web MVC | — | REST API |
| Spring Data JPA | — | Persistance, Specifications |
| Spring Security 6 | — | Authentification, RBAC |
| Spring AOP | — | Logging transversal |
| Hibernate ORM 6 | — | JPA Provider |
| PostgreSQL | 16 | Base de données |
| MapStruct | 1.5.5 | Mapping DTO ↔ Entités |
| Lombok | Latest | Boilerplate Java |
| jjwt | 0.12.5 | Génération/validation JWT |
| SpringDoc OpenAPI | 2.5.0 | Documentation Swagger |
| Spring Actuator | — | Health, Info |
| JUnit 5 + Mockito | — | Tests unitaires |
| Testcontainers | — | Tests d'intégration |
| Docker + Compose | — | Déploiement local |

---

## 🚀 Démarrage Rapide

### Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclut Docker Compose)
- Java 17+ et Maven 3.9+ (optionnel, pour développer/tester localement)

### Lancement avec Docker Compose (recommandé)

```bash
# 1. Cloner le dépôt
git clone <URL_REPO>
cd Developpement-Avanc-

# 2. Lancer l'application et la base de données
docker-compose up --build

# 3. Pour effacer les données et tout recréer
docker-compose down -v && docker-compose up --build
```

L'application est disponible sur **http://localhost:8080/api**

> ⚠️ **Context-path** : toutes les URLs sont préfixées par `/api` (configuré dans `application.yml`).

### Lancement local (avec PostgreSQL installé)

```bash
# Créer la base de données
createdb master_annonce

# Configurer les variables d'environnement
export DATABASE_URL=jdbc:postgresql://localhost:5432/master_annonce
export DB_USER=postgres
export DB_PASSWORD=postgres

# Lancer
mvn spring-boot:run
```

### Comptes de test (injectés via DataInitializer)

| Email | Mot de passe | Rôle |
|--|--|--|
| `admin@test.com` | `Admin1234!` | ADMIN |
| `user@user.com` | `password` | USER |

---

## 📡 Endpoints API

> **Base URL** : `http://localhost:8080/api`
> Pour tous les endpoints protégés, ajouter le header : `Authorization: Bearer <token>`

### 🔐 Authentification

| Méthode | Endpoint | Auth | Description |
|--|--|--|--|
| `POST` | `/api/auth/login` | ❌ | Login → retourne un JWT |

**Exemple de requête login :**
```json
POST /api/auth/login
{
  "email": "admin@test.com",
  "password": "Admin1234!"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "expiresIn": 3600,
  "userId": 1,
  "roles": ["ROLE_ADMIN"]
}
```

### 📣 Annonces

| Méthode | Endpoint | Auth | Rôle | Description |
|--|--|--|--|--|
| `GET` | `/annonces` | ✅ | USER/ADMIN | Liste paginée de toutes les annonces |
| `GET` | `/annonces/search` | ✅ | USER/ADMIN | Recherche avec filtres dynamiques |
| `GET` | `/annonces/{id}` | ✅ | USER/ADMIN | Détail d'une annonce |
| `POST` | `/annonces` | ✅ | USER/ADMIN | Créer une annonce (201 Created) |
| `PUT` | `/annonces/{id}` | ✅ | Auteur | Mise à jour complète (auteur uniquement) |
| `PATCH` | `/annonces/{id}` | ✅ | Auteur | Mise à jour partielle |
| `DELETE` | `/annonces/{id}` | ✅ | **ADMIN** | Supprimer une annonce (204 No Content) |

**Paramètres de recherche (`GET /annonces/search`) :**

| Paramètre | Type | Description |
|--|--|--|
| `q` | `String` | Recherche LIKE sur `title` et `description` |
| `status` | `DRAFT\|PUBLISHED\|ARCHIVED` | Filtre exact sur le statut |
| `categoryId` | `Long` | Filtre sur la catégorie |
| `authorId` | `Long` | Filtre sur l'auteur |
| `fromDate` | `LocalDate (yyyy-MM-dd)` | Borne inférieure de date |
| `toDate` | `LocalDate (yyyy-MM-dd)` | Borne supérieure de date |
| `page` | `int` | Numéro de la page (défaut: 0) |
| `size` | `int` | Taille de la page (défaut: 10) |

### 🗂️ Catégories

| Méthode | Endpoint | Auth | Rôle | Description |
|--|--|--|--|--|
| `GET` | `/categories` | ❌ | PUBLIC | Liste de toutes les catégories |
| `GET` | `/categories/{id}` | ❌ | PUBLIC | Détail d'une catégorie |
| `POST` | `/categories` | ✅ | **ADMIN** | Créer une catégorie (201) |
| `PUT` | `/categories/{id}` | ✅ | **ADMIN** | Modifier une catégorie |
| `DELETE` | `/categories/{id}` | ✅ | **ADMIN** | Supprimer une catégorie (204) |

### 🔬 Méta (Introspection)

| Méthode | Endpoint | Auth | Description |
|--|--|--|--|
| `GET` | `/meta/annonces` | ✅ | Liste les champs filtrables/triables de l'entité `Annonce` via réflexion Java |

---

## 🔐 Authentification JWT

### Flow

```
Client                    API
  │                         │
  │──POST /auth/login──────>│
  │   {email, password}     │
  │<──{token, roles}────────│
  │                         │
  │──GET /annonces──────────│
  │  Authorization: Bearer  │
  │             <token>     │
  │<──{data}────────────────│
```

### Configuration (`application.yml`)

```yaml
app:
  jwt:
    secret: <secret-256-bits>
    expiration: 3600  # 1h
```

### Claims du token JWT

| Claim | Description |
|--|--|
| `sub` | Email de l'utilisateur |
| `userId` | ID de l'utilisateur en base |
| `roles` | Liste des rôles (`ROLE_ADMIN`, `ROLE_USER`) |
| `exp` | Timestamp d'expiration |

---

## 🧩 Composants Clés

### 🔬 Recherche Dynamique (Specifications)

Implémentée dans `AnnonceSpecifications.java` avec `JpaSpecificationExecutor`. **Aucune JPQL géante** — chaque critère est une méthode `static` indépendante :

```java
AnnonceSpecifications.hasKeyword(q)       // LIKE sur title et description
AnnonceSpecifications.hasStatus(status)   // Égalité exacte
AnnonceSpecifications.hasCategoryId(id)   // Relation Category
AnnonceSpecifications.hasAuthorId(id)     // Relation User
AnnonceSpecifications.createdBetween(from, to) // Plage de dates
```

### 🗒️ Logging Transversal (AOP)

`LoggingAspect.java` intercepte **tous les appels de service** (`@Around`) :

```
[AnnonceService] create(Titre, Description...) - 45ms | OK
[AnnonceService] update(100, ...) - 12ms | ERROR: SecurityException - Not author
```

- Masquage automatique des paramètres sensibles (`password`, `token`, `secret`)
- Pas de lazy loading involontaire (on log le type Simple, pas `.toString()`)

### 🔗 Correlation ID

`CorrelationIdFilter.java` génère un UUID unique par requête, propagé via `MDC` :

```
[X-Correlation-ID: 3fa85f64-5717] INFO  AnnonceService - create() - 12ms | OK
```

### 🗺️ MapStruct

Toutes les conversions entité ↔ DTO passent par les mappers générés à la compilation :
- `AnnonceMapper` : `toDTO(Annonce)`, `toEntity(AnnonceCreateDTO)`, `updateFromDTO(@MappingTarget)`
- `CategoryMapper` : `toDTO(Category)`, `toEntity(CategoryDTO)`

---

## 🧪 Tests

### Tests unitaires (Mockito)

Fichier : `AnnonceServiceTest.java`

| Test | Vérifie |
|--|--|
| `findAll_ShouldReturnPagedList` | La pagination délègue au repository |
| `findById_WhenFound_...` | Retour de l'Optional correct |
| `create_ShouldSaveAndReturn` | Le mapper ET le repository sont appelés |
| `update_WhenAuthorMatches_...` | La mise à jour est persistée |
| `update_WhenAuthorDiffers_...` | Lève `SecurityException` |
| `delete_WhenNotArchived_...` | Lève `InvalidStateException` |
| `delete_WhenAuthorDiffers_...` | Lève `SecurityException` |

```bash
# Lancer uniquement les tests unitaires
mvn test
```

### Tests d'intégration (Testcontainers + MockMvc)

Fichier : `AnnonceIntegrationTest.java` — utilise `@SpringBootTest` + PostgreSQL via Testcontainers.

| Test | Scénario | Code attendu |
|--|--|--|
| `testLoginValidCredentials` | POST /auth/login avec bons identifiants | `200 + token` |
| `testGetAnnoncesWithoutToken` | GET /annonces sans Authorization | `401` |
| `testGetAnnoncesWithInvalidToken` | GET /annonces avec mauvais token | `401` |
| `testGetAnnoncesWithValidToken` | GET /annonces avec bon token | `200` |
| `testCreateAnnonceWithRoleUser` | POST /annonces avec ROLE_USER | `201` |
| `testDeleteAnnonceWithRoleUser` | DELETE /annonces avec ROLE_USER | `403` |
| `testDeleteAnnonceWithRoleAdmin` | DELETE /annonces avec ROLE_ADMIN | `204` |

```bash
# Lancer les tests d'intégration (Docker requis pour Testcontainers)
mvn verify
```

> ⚠️ Les tests d'intégration nécessitent Docker Desktop démarré sur la machine.

---

## 📖 Documentation OpenAPI / Swagger

- **Swagger UI** : [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)
- **OpenAPI JSON** : [http://localhost:8080/api/api-docs](http://localhost:8080/api/api-docs)

Le bouton **"Authorize"** permet de renseigner le token JWT (sans le préfixe `Bearer`).

Tous les endpoints sont annotés avec :
- `@Operation(summary=...)` — description de l'endpoint
- `@ApiResponse(responseCode=...)` — codes de retour attendus (200, 201, 400, 401, 403, 404)
- `@Parameter(description=...)` — description des paramètres

---

## 🩺 Monitoring Actuator

| Endpoint | Description |
|--|--|
| `GET /actuator/health` | Statut de l'application (UP/DOWN) + santé PostgreSQL |
| `GET /actuator/info` | Métadonnées (nom, version) |

**URLs complètes :**
- [http://localhost:8080/api/actuator/health](http://localhost:8080/api/actuator/health)
- [http://localhost:8080/api/actuator/info](http://localhost:8080/api/actuator/info)

```json
// GET /actuator/health
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## 🐳 Docker & Déploiement

### Architecture Docker

```
┌─────────────────────┐     ┌──────────────────────┐
│   master_annonce_app│────▶│  master_annonce_db   │
│   (Spring Boot)     │     │  (PostgreSQL 16)     │
│   port: 8080        │     │  port: 5432          │
│                     │     │  volume: postgres_data│
└─────────────────────┘     └──────────────────────┘
```

### Dockerfile (multi-stage)

| Stage | Image | Description |
|--|--|--|
| `build` | `maven:3.9-eclipse-temurin-17` | Compilation Maven, génération du `.jar` |
| `runtime` | `eclipse-temurin:17-jre-alpine` | Image finale légère (JRE only, ~100Mo) |

**Sécurité** : l'application tourne sous un utilisateur `spring:spring` (non-root).
**Healthcheck** : vérifie `/actuator/health` toutes les 30s.

### Variables d'environnement

| Variable | Défaut | Description |
|--|--|--|
| `DATABASE_URL` | `jdbc:postgresql://db:5432/master_annonce` | URL JDBC |
| `DB_USER` | `postgres` | Username PostgreSQL |
| `DB_PASSWORD` | `postgres` | Password PostgreSQL |
| `APP_JWT_SECRET` | *(valeur dans app)* | Clé secrète JWT 256-bit |

---

## 🤖 CI/CD — GitHub Actions

**Fichier** : `.github/workflows/ci.yml`

### Déclencheurs

- ✅ `push` sur **toutes les branches**
- ✅ `pull_request` vers `main`

### Jobs

```
build-and-test ─────────────────────────────── docker-build (main only)
│                                                 │
│  Java 17 + Java 21 (matrix)                    │  Build master-annonce:latest
│  mvn -B clean verify (JAMAIS -DskipTests)       │
│  Upload artifact "master-annonce-jar"           │
```

### Choix de la stratégie DB : Testcontainers ✅

Nous utilisons **Testcontainers** (Option 1 recommandée) plutôt qu'un service PostgreSQL déclaré dans le workflow.

**Justification :**

1. **Reproductibilité parfaite** — Chaque test démarre sa propre instance PostgreSQL vierge dans un conteneur temporaire. Plus aucun risque de pollution de données entre tests.

2. **Cohérence dev-local/CI** — Le développeur qui lance `mvn verify` sur son poste utilise exactement le même mécanisme que la CI. La maxime "ça marche sur ma machine" disparaît.

3. **Simplification du workflow** — Pas de bloc `services:` à maintenir dans le YAML GitHub Actions. La configuration DB est 100% dans le code Java.

4. **Isolation des ports** — Testcontainers choisit un port aléatoire pour chaque test, évitant les conflits sur les runners GitHub.

**Artefact produit** : `master-annonce-jar` (téléchargeable depuis l'onglet "Actions" du workflow)

---

## 🐞 Problèmes Rencontrés & Solutions

### 1. Règles métier d'accès manquantes

**Problème** : Les contrôleurs checkaient l'authentification, mais la vérification "seul l'auteur peut modifier" était absente.
**Solution** : Implémentation d'une méthode `checkOwnership()` dans `AnnonceService`, levant une `SecurityException` si `annonce.getAuthor().getId() != userId`. Contrôle du statut (`PUBLISHED` → non modifiable) dans la même couche service.

### 2. Erreur `BadCredentials` au login après `docker-compose up --build`

**Problème** : Le `DataInitializer` re-crée les utilisateurs à chaque démarrage. Si le schéma existait déjà avec des données différentes, des conflits de mots de passe BCrypt pouvaient survenir.
**Solution** : Utilisation de `docker-compose down -v` pour effacer le volume PostgreSQL avant un redémarrage propre. `DataInitializer` vérifie maintenant si l'utilisateur existe (`findByEmail`) avant d'en créer un nouveau.

### 3. `LazyInitializationException` sur les entités JPA

**Problème** : La sérialisation JSON (`AnnonceDTO`) accédait à `annonce.getAuthor().getUsername()` après la fermeture de la session Hibernate.
**Solution** : Ajout de requêtes avec `JOIN FETCH` dans `AnnonceRepository` (`findAllWithDetails`, `findByIdWithDetails`).

### 4. Tests d'intégration Testcontainers échouent en local

**Problème** : Si Docker Desktop n'est pas démarré, les tests Testcontainers lancent l'erreur `Could not find a valid Docker environment`.
**Solution** : Les tests d'intégration sont séparés (`*IntegrationTest.java`) et n'ont pas vocation à tourner sans Docker. Dans la CI, Docker est disponible sur les runners `ubuntu-latest`.

### 5. Gestion des imports SpringDoc dans SecurityConfig

**Problème** : Swagger UI renvoyait une `401 Unauthorized`, les routes Swagger n'étaient pas ouvertes.
**Solution** : Ajout explicite des path patterns `/v3/api-docs/**`, `/swagger-ui/**`, et `/swagger-ui.html` dans la liste `permitAll()` de `SecurityFilterChain`.

> ℹ️ **Note** : Les erreurs `401` visibles dans les logs lorsqu'on accède à Swagger UI sont **normales**. Swagger effectue quelques sondes sans token pour découvrir les endpoints publics avant de charger l'UI.

### 6. Schéma PostgreSQL incompatible après mise à jour Spring Boot 3.4.3

**Problème** : Lors du passage de Spring Boot `3.2.5` à `3.4.3`, la version d'Hibernate est passée de `6.4` à `6.6`. Hibernate 6.6 effectue une validation de schéma plus stricte (`ddl-auto: validate`) et rejetait le schéma existant, causant un crash au démarrage (`org.hibernate.tool.schema.spi.SchemaManagementException`).
**Solution** : Migration vers `ddl-auto: update`. Hibernate adapte automatiquement le schéma existant sans le détruire. Toujours lancer `docker-compose down -v` avant un rebuild propre.

### 7. Avertissement `version` obsolète dans docker-compose.yml

**Problème** : Docker Compose v2+ ne reconnaît plus le champ `version: '3.8'` au niveau racine du fichier. Il l'ignore avec un avertissement.
**Solution** : Suppression de la clé `version` du `docker-compose.yml`.

---

## 📦 Livrables

| Livrable | Statut | Localisation |
|--|--|--|
| Projet Maven complet | ✅ | Ce repository |
| README complet | ✅ | `README.md` |
| `docker-compose.yml` | ✅ | Racine du projet |
| `Dockerfile` multi-stage | ✅ | Racine du projet |
| CI GitHub Actions | ✅ | `.github/workflows/ci.yml` |
| Documentation Swagger | ✅ | `/swagger-ui/index.html` |
| Tests unitaires (Mockito) | ✅ | `src/test/.../service/AnnonceServiceTest.java` |
| Tests d'intégration (MockMvc+TC) | ✅ | `src/test/.../integration/AnnonceIntegrationTest.java` |

---

## 📄 Licence

Projet universitaire – BUT 3 S6.R5 – Développement Avancé 2025
