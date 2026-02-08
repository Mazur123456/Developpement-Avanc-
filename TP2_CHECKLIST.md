# TP2 - Modernisation JPA/Hibernate - Checklist

## 📊 Progression globale

| Exercice | État | Description |
|----------|------|-------------|
| Exercice 1 | ✅ Terminé | Mise en place JPA/Hibernate |
| Exercice 2 | ✅ Terminé | Mapping des entités JPA |
| Exercice 3 | ✅ Terminé | Couche Repository (DAO JPA) |
| Exercice 4 | ✅ Terminé | Couche Service & Transactions |
| Exercice 5 | ✅ Terminé | Refonte Web (Servlets & JSP) |
| Exercice 6 | ✅ Terminé | Validation et gestion erreurs |
| Bonus | ⏳ En attente | Tests unitaires et intégration |

---

## Exercice 1 – Mise en place de JPA / Hibernate

- [ ] Ajouter les dépendances JPA/Hibernate dans `pom.xml`
- [ ] Créer le fichier `persistence.xml`
- [ ] Configurer la connexion PostgreSQL
- [ ] Créer la classe utilitaire `JPAUtil` (EntityManager)
- [ ] Tester que JPA démarre correctement

**Livrable** : Application démarrable avec JPA opérationnel

---

## Exercice 2 – Mapping des entités JPA

- [ ] Créer l'entité `User` (id, username, email, password, createdAt)
- [ ] Créer l'entité `Category` (id, label)
- [ ] Créer l'enum `AnnonceStatus` (DRAFT, PUBLISHED, ARCHIVED)
- [ ] Modifier l'entité `Annonce` (ajouter status, author, category)
- [ ] Définir les relations `@ManyToOne` / `@OneToMany`
- [ ] Ajouter les contraintes Bean Validation (`@NotNull`, `@Size`)
- [ ] Vérifier la génération du schéma BDD par Hibernate

**Livrable** : Schéma BDD généré/validé par Hibernate

---

## Exercice 3 – Couche Repository (DAO JPA)

- [ ] Créer `UserRepository` (CRUD)
- [ ] Créer `CategoryRepository` (CRUD)
- [ ] Créer `AnnonceRepository` avec :
  - [ ] CRUD complet
  - [ ] Recherche par mot-clé (JPQL)
  - [ ] Filtrage par catégorie et statut
  - [ ] Pagination des résultats
- [ ] Vérifier : utilisation exclusive de JPQL (pas de JDBC)

---

## Exercice 4 – Couche Service & Transactions

- [ ] Créer `UserService`
- [ ] Créer `CategoryService`
- [ ] Créer `AnnonceService` avec :
  - [ ] Création d'annonce
  - [ ] Modification
  - [ ] Publication (DRAFT → PUBLISHED)
  - [ ] Archivage (PUBLISHED → ARCHIVED)
  - [ ] Suppression
  - [ ] Recherche et listing paginé
- [ ] Gérer les transactions dans la couche service
- [ ] Vérifier : pas de transaction dans les Servlets

---

## Exercice 5 – Refonte Web (Servlets & JSP)

### Authentification
- [ ] Créer servlet `LoginServlet`
- [ ] Créer servlet `LogoutServlet`
- [ ] Créer `login.jsp`
- [ ] Créer filtre de sécurité `AuthFilter`
- [ ] Stocker l'utilisateur en session

### Fonctionnalités Web
- [ ] Liste paginée des annonces
- [ ] Formulaire de création d'annonce
- [ ] Formulaire de modification d'annonce
- [ ] Page de détail d'une annonce
- [ ] Actions Publish / Archive selon le statut

---

## Exercice 6 – Validation et gestion des erreurs

- [x] Validation serveur des formulaires
- [x] Affichage des messages d'erreur dans les JSP
- [x] Conservation des valeurs saisies en cas d'erreur
- [x] Documenter les problèmes rencontrés dans README

---

## ⭐ Bonus – Tests

### Niveau 1 – Tests Repository (intégration)
- [ ] Tests CRUD avec base réelle
- [ ] Tests de recherche et pagination

### Niveau 2 – Tests Service (unitaires)
- [ ] Mockito pour mocker les repositories
- [ ] Tests des règles métier

### Niveau 3 – Tests d'intégration métier
- [ ] Enchaînement : création → publication → recherche
- [ ] Tests Lazy / N+1

### Niveau 4 – Tests Web
- [ ] Tests de Servlets avec mocks HTTP
- [ ] Test du filtre d'authentification

---

## 📝 Livrables finaux

- [ ] Projet Maven complet
- [ ] README avec architecture et problèmes rencontrés
- [ ] Scripts SQL
- [ ] Tests automatisés
