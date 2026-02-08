# 🐳 Guide de Démarrage Rapide avec Docker

Ce guide explique comment lancer l'application **Master Annonce** et sa base de données PostgreSQL en utilisant Docker, comme configuré dans votre `docker-compose.yml` existant.

## 📋 Prérequis

*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et lancé.
*   Maven (pour compiler le projet avant de construire l'image Docker).

---

## 🚀 Lancer l'application

Suivez ces étapes dans votre terminal (à la racine du projet `.../Developpement-Avanc-`) :

### 1. Clôturer les anciens services
Si vous avez des conteneurs qui tournent déjà, arrêtez-les proprement pour éviter les conflits de ports :
```bash
docker-compose down
```

### 2. Compiler l'application
Générez le fichier WAR (`TP_JEE_1-1.0-SNAPSHOT.war`) que Docker va utiliser :
```bash
mvn clean package
```
*Si vous n'avez pas Maven dans votre PATH, utilisez `./mvnw clean package` sous Linux/Mac ou `mvnw.cmd clean package` sous Windows.*

### 3. Construire et Démarrer les conteneurs
Cette commande construit l'image de l'application (basée sur le Dockerfile que j'ai ajouté) et lance la base de données :
```bash
docker-compose up --build
```
*L'option `--build` force la reconstruction de l'image Tomcat pour inclure votre nouveau code.*

### 4. Attendre le démarrage
Vous verrez les logs défiler. Attendez de voir une ligne indiquant que Tomcat a démarré (ex: `org.apache.catalina.startup.Catalina.start Server startup in [xxxx] ms`).

---

## 🌐 Accéder à l'application

Une fois démarré, ouvrez votre navigateur :

*   **Accueil / Login** :
    [http://localhost:8080/login](http://localhost:8080/login)

*   **Liste des annonces** :
    [http://localhost:8080/annonces](http://localhost:8080/annonces)

*   **Base de données (si besoin)** :
    *   Hôte : `localhost`
    *   Port : `5432`
    *   User : `postgres`
    *   Pass : `postgres`
    *   DB : `master_annonce`

---

## 🛠 Commandes Utiles

| Action | Commande |
|--------|----------|
| **Arrêter** (Ctrl+C ne suffit pas toujours) | `docker-compose down` |
| **Relancer après modification de code** | 1. `mvn clean package`<br>2. `docker-compose up --build` |
| **Voir les logs** | `docker-compose logs -f` |
| **Nettoyer tout (volumes DB inclus)** | `docker-compose down -v` *(Attention : supprime les données !)* |

---

## ❓ Dépannage

*   **Erreur "Port already allocated"** : Un autre service (ou une instance locale de Tomcat/PostgreSQL) utilise déjà le port 8080 ou 5432. Arrêtez vos serveurs locaux (`shutdown.bat` pour Tomcat) ou changez les ports dans `docker-compose.yml`.
*   **Erreur de connexion DB** : Assurez-vous que le conteneur `db` est bien "healthy" ou démarré avant que Tomcat n'essaie de s'y connecter. Le `depends_on` dans le fichier compose gère l'ordre de démarrage, mais pas l'attente de la disponibilité complète du port. Si ça échoue au premier lancement, attendez quelques secondes et réessayez.
