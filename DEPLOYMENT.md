# 🚀 Guide de Déploiement Docker

## Prérequis
- Docker Desktop installé et démarré
- Maven installé
- Java 11+

---

## 🎯 Commandes à exécuter dans l'ordre

### 1. Compiler le projet Maven
```bash
mvn clean package -DskipTests
```

### 2. Construire et lancer les containers Docker
```bash
docker-compose up --build -d
```

### 3. Accéder à l'application
```
http://localhost:8080/
```

---

## 📌 Commandes Docker utiles

| Action | Commande |
|--------|----------|
| Voir les containers actifs | `docker ps` |
| Voir les logs en temps réel | `docker-compose logs -f` |
| Arrêter les containers | `docker-compose down` |
| Arrêter et supprimer les volumes | `docker-compose down -v` |
| Redémarrer après modification | `mvn clean package -DskipTests && docker-compose up --build -d` |

---

## 🔧 Configuration

### Ports
| Service | Port |
|---------|------|
| Tomcat | **8080** |
| PostgreSQL | **5432** |

### Base de données
| Paramètre | Valeur |
|-----------|--------|
| Host | db (Docker) / localhost (local) |
| Database | master_annonce |
| User | postgres |
| Password | postgres |
