# Stage 1 : Build de l'application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On package le JAR (en sautant les tests pour l'image Docker)
RUN mvn clean package -DskipTests

# Stage 2 : Runtime (Image finale allégée)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sécurité : Création d'un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# On copie le JAR généré à l'étape 1 (le *.jar gère les noms dynamiques)
COPY --from=build /app/target/*.jar app.jar

# On expose le port
EXPOSE 8080

# Healthcheck Actuator (demandé à l'étape 12)
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q -O - http://localhost:8080/actuator/health || exit 1

# Démarrage de l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]