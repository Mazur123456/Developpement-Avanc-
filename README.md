# MasterAnnonce - Application de Gestion de Petites Annonces

Application Java EE modernisée avec JPA/Hibernate, architecture en couches (Servlet/JSP/Service/Repository).

## 🎓 Contexte

Projet universitaire TP2 – Modernisation d'une application Web Java EE avec JPA/Hibernate.

## 📦 Technologies

- **Java 11** + **Maven**
- **JPA 2.2** (javax.persistence) + **Hibernate ORM 5.6**
- **PostgreSQL** (via driver JDBC 42.7)
- **Servlet API 4.0.1** + **JSTL 1.2** + **JSP**
- **Bean Validation** (Hibernate Validator 6.2)
- **Bootstrap 5.3.0** (CDN)

## 🏗️ Architecture

Architecture en couches conformément aux exigences du TP2 :

```
src/main/java/.../tp_jee_1/
├── entity/         # Entités JPA (User, Category, Annonce, AnnonceStatus)
├── repository/     # Couche d'accès aux données (JPQL, pas de transactions)
├── service/        # Couche métier (gestion des transactions)
├── servlet/        # Contrôleurs HTTP (Login, Register, CRUD Annonce)
├── filter/         # Filtre d'authentification (AuthFilter)
├── listener/       # Lifecycle JPA (JPAContextListener)
├── exception/      # Exceptions métier personnalisées
└── utils/          # JPAUtil (EntityManager), ValidationUtil (Bean Validation)
```

### Séparation des responsabilités

| Couche | Rôle | Transactions |
|--------|------|-------------|
| **Servlet** | Traitement HTTP, validation basique, routing JSP | ❌ Aucune |
| **Service** | Logique métier, orchestration | ✅ Gérées ici |
| **Repository** | Accès données JPQL, reçoit l'EntityManager | ❌ Aucune |

## 🗄️ Modèle de données

### Entités JPA

- **User** : `id`, `username` (unique), `email` (unique), `password`, `createdAt`
- **Category** : `id`, `label` (unique)
- **Annonce** : `id`, `title`, `description`, `adress`, `mail`, `date`, `status` (ENUM), `author` → User, `category` → Category

### Relations

- `User` 1─N `Annonce` (`@OneToMany` / `@ManyToOne`, LAZY)
- `Category` 1─N `Annonce` (`@OneToMany` / `@ManyToOne`, LAZY)

## 🚀 Installation et Déploiement

### Prérequis

- Java 11+, Maven 3+, PostgreSQL, Tomcat 9

### 1. Base de données

```sql
CREATE DATABASE master_annonce;
```

> Hibernate génère automatiquement le schéma (`hbm2ddl.auto=update`).

### 2. Configuration

Modifier `src/main/resources/META-INF/persistence.xml` si nécessaire (URL, user, password).

### 3. Compilation

```bash
mvn clean package
```

### 4. Déploiement Docker

```bash
docker-compose up --build
```

### 5. Accéder à l'application

```
http://localhost:8080/login
```

## 📱 Fonctionnalités

- **Authentification** : Login/Register avec session HTTP + filtre de sécurité (`AuthFilter`)
- **Liste paginée** des annonces publiées (`/annonces`)
- **Création** d'annonce avec sélection de catégorie (`/annonce/create`)
- **Modification** d'annonce (`/annonce/edit?id=X`)
- **Détail** d'annonce avec JOIN FETCH (`/annonce/detail?id=X`)
- **Publication** (DRAFT → PUBLISHED) et **Archivage** (PUBLISHED → ARCHIVED)
- **Validation** serveur (Bean Validation JSR-380) avec conservation des valeurs saisies

## 🐛 Problèmes rencontrés et solutions

### 1. LazyInitializationException sur les relations

**Problème** : Accéder à `annonce.getAuthor()` ou `annonce.getCategory()` après la fermeture de l'EntityManager lançait une `LazyInitializationException`.

**Solution** : Utilisation de `JOIN FETCH` dans les requêtes JPQL (méthode `findByIdWithDetails()`) pour charger les relations dans la même requête.

### 2. Gestion du cycle de vie de l'EntityManagerFactory

**Problème** : L'EntityManagerFactory n'était pas fermé proprement à l'arrêt de l'application, causant des fuites de connexions.

**Solution** : Mise en place d'un `JPAContextListener` (`@WebListener`) qui appelle `JPAUtil.shutdown()` sur `contextDestroyed`.

### 3. Transactions et architecture en couches

**Problème** : Initialement, les transactions étaient gérées à la fois dans les Repositories et les Services, créant une confusion architecturale.

**Solution** : Refactorisation pour que les Repositories reçoivent l'EntityManager en paramètre (sans gestion de transaction), et que les Services gèrent exclusivement les transactions (begin/commit/rollback).

### 4. Configuration Docker vs Local

**Problème** : Les paramètres de connexion diffèrent entre l'environnement local et Docker.

**Solution** : `JPAUtil` lit les variables d'environnement Docker (`DB_HOST`, `DB_PORT`, etc.) et surcharge les valeurs de `persistence.xml` si elles sont présentes.

## 📄 Licence

Projet universitaire - BUT 3 S6.R5
