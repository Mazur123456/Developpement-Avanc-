FROM tomcat:9.0-jdk11

# Supprimer les applications par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le WAR dans Tomcat (renommé en ROOT.war pour être accessible à la racine)
COPY target/TP_JEE_1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
