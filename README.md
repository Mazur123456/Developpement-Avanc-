# MasterAnnonce - Application de Gestion de Petites Annonces

Application Java EE développée selon l'architecture MVC (Servlet/JSP) avec JDBC pur.

## 🎓 Contexte

Projet universitaire développé selon les spécifications du cours, utilisant:
- **Architecture**: MVC sans framework lourd (pas de Spring/Hibernate)
- **Base de données**: PostgreSQL avec JDBC pur
- **Frontend**: JSP + JSTL + Bootstrap 5
- **Serveur**: Apache Tomcat 9

## 📦 Technologies

- **Java 11**
- **Maven** (gestion de dépendances)
- **PostgreSQL** (base de données)
- **Servlet API 4.0.1**
- **JSTL 1.2**
- **Bootstrap 5.3.0**

## 🏗️ Architecture

```
fr.paris13.master/
├── bean/          # Objets métier (Annonce)
├── dao/           # Data Access Objects (DAO, AnnonceDAO)
├── utils/         # Utilitaires (ConnectionDB Singleton)
└── servlet/       # Contrôleurs (AnnonceList, AnnonceAdd, AnnonceUpdate, AnnonceDelete)
```

## 🗄️ Base de données

### Configuration PostgreSQL

1. Créer la base de données:
```sql
CREATE DATABASE MasterAnnonce;
```

2. Se connecter et exécuter le script `src/main/resources/init.sql`:
```sql
\c MasterAnnonce
CREATE TABLE annonce (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64),
    description VARCHAR(256),
    adress VARCHAR(64),
    mail VARCHAR(64),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Paramètres de connexion

Par défaut dans `ConnectionDB.java`:
- **URL**: `jdbc:postgresql://localhost:5432/MasterAnnonce`
- **User**: `postgres`
- **Password**: `password13`

> ⚠️ **Important**: Modifiez ces paramètres si nécessaire dans `src/main/java/fr/paris13/master/utils/ConnectionDB.java`

## 🚀 Installation et Déploiement

### 1. Compilation

```bash
mvn clean package
```

### 2. Déploiement sur Tomcat

Copier le fichier WAR généré dans le dossier `webapps/` de Tomcat:
```bash
cp target/MasterAnnonce-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps/
```

### 3. Démarrer Tomcat

```bash
$TOMCAT_HOME/bin/startup.sh  # Linux/Mac
$TOMCAT_HOME/bin/startup.bat  # Windows
```

### 4. Accéder à l'application

Ouvrir dans le navigateur:
```
http://localhost:8080/MasterAnnonce-1.0-SNAPSHOT/
```

## 📱 Fonctionnalités

### 1. Liste des annonces (`/AnnonceList`)
- Affichage de toutes les annonces en cartes
- Boutons "Modifier" et "Supprimer" pour chaque annonce
- Bouton "Ajouter une annonce"

### 2. Ajouter une annonce (`/AnnonceAdd`)
- Formulaire avec validation
- Champs: Titre, Description, Adresse, Email

### 3. Modifier une annonce (`/AnnonceUpdate?id=X`)
- Formulaire pré-rempli avec les données existantes
- Mise à jour des informations

### 4. Supprimer une annonce (`/AnnonceDelete?id=X`)
- Suppression avec confirmation JavaScript
- Redirection vers la liste

## 🔧 Développement

### Structure du projet

```
project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/org/univ_paris8/iut/montreuil/qdev/tp2025/gr/tpjee/tp_jee_1/
│   │   │   ├── bean/Annonce.java
│   │   │   ├── dao/
│   │   │   │   ├── DAO.java
│   │   │   │   └── AnnonceDAO.java
│   │   │   ├── servlet/
│   │   │   │   ├── AnnonceList.java
│   │   │   │   ├── AnnonceAdd.java
│   │   │   │   ├── AnnonceUpdate.java
│   │   │   │   └── AnnonceDelete.java
│   │   │   └── utils/ConnectionDB.java
│   │   ├── resources/init.sql
│   │   └── webapp/
│   │       ├── AnnonceList.jsp
│   │       ├── AnnonceAdd.jsp
│   │       ├── AnnonceUpdate.jsp
│   │       └── index.jsp
│   └── test/
└── target/
```

### Patterns utilisés

- **Singleton**: `ConnectionDB` pour la gestion unique de la connexion
- **DAO (Data Access Object)**: Séparation de la logique d'accès aux données
- **MVC**: Servlet (Contrôleur) + JSP (Vue) + Bean (Modèle)

## 🛡️ Sécurité

- Utilisation de **PreparedStatement** pour éviter les injections SQL
- Échappement JSTL avec `<c:out>` pour éviter les failles XSS
- Validation HTML5 sur les formulaires

## 📝 Notes

- Le projet utilise JDBC pur (pas d'ORM comme Hibernate)
- Les annotations `@WebServlet` sont utilisées pour le mapping des servlets
- Bootstrap 5 est chargé via CDN

## 📄 Licence

Projet universitaire - BUT 3 S6.R5
