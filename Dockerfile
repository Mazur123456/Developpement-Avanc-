FROM tomcat:9-jdk11-openjdk

# Supprimer les applications par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le fichier WAR généré dans le dossier webapps de Tomcat
# Le nom du fichier WAR doit correspondre à celui généré par Maven (voir pom.xml)
COPY target/TP_JEE_1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Exposer le port 8080
EXPOSE 8080

# Démarrer Tomcat
CMD ["catalina.sh", "run"]
