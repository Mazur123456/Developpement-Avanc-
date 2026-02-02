Instructions pour le projet Java EE "MasterAnnonce"
Rôle : Tu es un développeur expert en Java EE, spécialisé en architecture MVC (Servlet/JSP) et JDBC. Objectif : Générer le code complet d'une application de gestion de petites annonces appelée MasterAnnonce. Contexte : Ce projet est un TP universitaire qui doit respecter strictement les patterns vus en cours (Singleton pour la DB, DAO pour les données).

1. Stack Technique
Langage : Java (compatible Java 8+)

Build : Maven

Serveur : Apache Tomcat (cible Tomcat 9)

Base de données : PostgreSQL

Frontend : JSP + JSTL + Bootstrap (via CDN)

Architecture : Modèle MVC sans framework lourd (pas de Spring/Hibernate), utilisation de JDBC pur.

2. Structure du Projet (Packages)
L'application doit utiliser le groupId : fr.paris13.master. Les packages Java doivent être :

fr.paris13.master.bean (Objets métier)

fr.paris13.master.dao (Accès aux données)

fr.paris13.master.utils (Connexion BDD)

fr.paris13.master.servlet (Contrôleurs)

3. Base de données
La base de données se nomme MasterAnnonce. Voici le script SQL de création de la table que tu dois supposer existante (ou inclure dans un fichier init.sql) :

SQL
CREATE TABLE annonce (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64),
    description VARCHAR(256),
    adress VARCHAR(64),
    mail VARCHAR(64),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
4. Détails d'implémentation
A. Configuration (pom.xml)
Génère un pom.xml incluant les dépendances suivantes :

javax.servlet-api (scope provided)

jstl (implémentation de taglibs standard)

postgresql (driver JDBC)

B. Connexion BDD (Singleton)
Dans fr.paris13.master.utils.ConnectionDB, implémente le pattern Singleton exactement comme suit (adapté du cours) :

URL : jdbc:postgresql://localhost:5432/MasterAnnonce

User : postgres

Pass : password13 (ou une variable d'environnement si possible)

La classe doit charger le driver org.postgresql.Driver.

Elle doit avoir une méthode statique getInstance() qui retourne l'objet Connection.

C. Le Modèle (Bean)
Dans fr.paris13.master.bean.Annonce :

Attributs privés : id (int), title (String), description (String), adress (String), mail (String), date (Timestamp).

Constructeurs (vide et complet).

Getters et Setters.

D. Le DAO (Data Access Object)
Crée une classe abstraite DAO<T> dans fr.paris13.master.dao définissant les méthodes : find(id), create(obj), update(obj), delete(obj), findAll().

Crée AnnonceDAO qui hérite de DAO<Annonce>.

Utilise ConnectionDB.getInstance() pour obtenir la connexion.

Utilise des PreparedStatement pour toutes les requêtes SQL (sécurité).

E. Les Servlets (Contrôleurs)
Utilise l'annotation @WebServlet pour le mapping.

AnnonceList (/AnnonceList) :

Appelle AnnonceDAO.findAll().

Stocke la liste dans l'attribut de requête "annonces".

Forward vers /WEB-INF/jsp/AnnonceList.jsp (ou racine si structure simple).

AnnonceAdd (/AnnonceAdd) :

GET : Affiche le formulaire AnnonceAdd.jsp.

POST : Récupère les champs, crée l'objet Annonce, appelle le DAO pour sauvegarder, puis redirige vers AnnonceList.

AnnonceUpdate (/AnnonceUpdate) :

GET : Récupère l'ID en paramètre, charge l'annonce via DAO, la met en attribut et affiche AnnonceUpdate.jsp.

POST : Récupère les données modifiées et met à jour via DAO.

AnnonceDelete (/AnnonceDelete) :

Récupère l'ID et supprime l'annonce via DAO.

F. Les Vues (JSP)
Utilise Bootstrap (CDN) pour un design propre. Utilise JSTL (<c:forEach>, <c:out>) pour l'affichage.

AnnonceList.jsp : Affiche un tableau des annonces avec des boutons "Modifier" et "Supprimer" pour chaque ligne, et un bouton global "Ajouter".

AnnonceAdd.jsp : Formulaire avec les champs (Titre, Description, Adresse, Mail).

AnnonceUpdate.jsp : Idem que Add, mais pré-rempli avec les valeurs existantes.

5. Instructions d'exécution pour l'IA
Crée l'arborescence des fichiers standard Maven (src/main/java, src/main/webapp, etc.).

Écris le contenu de tous les fichiers Java, XML et JSP mentionnés ci-dessus.

Assure-toi que le code compile.

Action requise : Génère maintenant l'ensemble des fichiers du projet.