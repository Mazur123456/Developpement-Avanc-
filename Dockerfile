# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Optimisation cache: copier d'abord le pom.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le reste du projet
COPY src ./src

# Compiler le projet et créer le jar exécutable (on saute les tests pour l'étape de construction Docker)
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sécurité : Création d'un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le jar compilé depuis l'étape de construction
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Healthcheck via l'endpoint Actuator configuré précédemment
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]