# RT-ComOps API

Backend unifié pour la plateforme RT-ComOps, basé sur une **Architecture Hexagonale**, **Spring Boot 3 (WebFlux)** et **R2DBC**.

## 🏗 Architecture

Le projet suit strictement l'architecture hexagonale (Ports & Adapters) pour isoler la logique métier des détails techniques.

### Structure du Projet (`src/main/java/yowyob/comops/api`)

*   **`domain`** : Le cœur du système. Contient la logique métier pure, sans aucune dépendance framework (pas de Spring, pas de JPA).
    *   **`model`** : Les objets du domaine (Entités métier).
    *   **`port`** : Les interfaces définissant les entrées (`in`) et sorties (`out`) du domaine.
        *   `in` (Use Cases) : Ce que l'application peut faire.
        *   `out` (Repositories/Gateways) : Ce dont l'application a besoin (DB, APIs externes).
    *   **`service`** : L'implémentation des Use Cases (`port.in`). C'est ici que réside la logique applicative.

*   **`infrastructure`** : Les détails techniques qui implémentent les ports ou appellent les use cases.
    *   **`adapter`** :
        *   **`in.web`** : Contrôleurs REST (WebFlux) qui exposent l'API.
        *   **`out.persistence`** : Implémentation R2DBC des repositories.
    *   **`config`** : Configuration Spring (Sécurité, Swagger, CORS, etc.).

## 🧩 Modules Principaux

### 1. IAM & Sécurité (`security`)
Gestion des utilisateurs, rôles, permissions et authentification JWT.
- **Endpoints** : `/api/v1/auth`, `/api/v1/users`

### 2. Organisation (`organization`)
Gestion de la structure de l'entreprise : Agences, Employés, Horaires d'ouverture.
- **Endpoints** : `/api/v1/organizations`, `/api/v1/agencies`, `/api/v1/employees`

### 3. Gestion des Tiers (`thirdparty`) **[NOUVEAU]**
Module unifié remplaçant l'ancienne gestion des tiers.
- **Clients** : `/api/v1/customers`
- **Fournisseurs** : `/api/v1/suppliers`
- **Prospects** : `/api/v1/prospects`
- **Commerciaux** : `/api/v1/sales-agents`

### 4. Configuration & Utilitaires
Gestion des fichiers (`file`) et options générales (`config`).

## 🛠 Stack Technique

- **Framework** : Spring Boot 3.2 (Reactive Stack)
- **Langage** : Java 17
- **Base de Données** : PostgreSQL 15+
- **Accès Données** : R2DBC (Reactive Relational Database Connectivity)
- **Migration** : Liquibase (via JDBC au démarrage)
- **API Doc** : OpenAPI 3 (Swagger UI)

## 🚀 Démarrage

### Pré-requis
- Java 17
- Docker (pour la DB) ou PostgreSQL local
- Variables d'environnement configurées (voir `application.properties`)

### Configuration Base de Données
Le projet utilise vos variables d'environnement existantes :
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=yowyob
DB_USERNAME=...
DB_PASSWORD=...
```

### Lancer l'application
```bash
./mvnw spring-boot:run
```

### Documentation API & Tests
Une fois lancé, accédez à Swagger UI pour explorer et tester les endpoints :
👉 http://localhost:8080/swagger-ui.html

## 🔄 Migration de Données
Les scripts de migration sont dans `src/main/resources/db/changelog`.
- `001` à `007` : Schémas Core API (IAM, Org, etc.)
- `008` : Schéma Tiers Unifié (Clients, Fournisseurs, etc.)

L'application applique automatiquement les changements au démarrage.
