# ComOps - Core API

Bienvenue sur l'API Core de ComOps. Ce service est le cœur de l'architecture microservices de l'ERP ComOps. Il est responsable de la gestion des identités, des organisations, des permissions et des configurations transversales.

## Table des Matières
1. [Fonctionnalités Clés](#fonctionnalités-clés)
2. [Prérequis](#prérequis)
3. [Installation et Lancement](#installation-et-lancement)
4. [Accès à l'API (Swagger)](#accès-à-lapi-swagger)
5. [Concepts Clés](#concepts-clés)
    - [Authentification (JWT)](#authentification-jwt)
    - [Multi-Tenant (X-Tenant-ID)](#multi-tenant-x-tenant-id)
6. [Scénario d'Utilisation Typique](#scénario-dutilisation-typique)
7. [Structure des Endpoints Principaux](#structure-des-endpoints-principaux)


## Fonctionnalités Clés

Ce service gère les fondations de l'ERP :
- **Gestion des Identités (IAM)**: Inscription, connexion, gestion des utilisateurs, rôles et permissions.
- **Structure Organisationnelle**: Gestion des "Business Actors" (propriétaires), des organisations (tenants) et de leur hiérarchie (agences, entrepôts, siège social).
- **Ressources Humaines**: Création et gestion des membres (employés) au sein d'une organisation.
- **Configuration Globale**: Paramètres applicables à une organisation ou une agence (préfixes, règles métier).
- **Logistique Statique**: Horaires d'ouverture, jours fériés, points d'intérêt géographiques.
- **Services Utilitaires**: Stockage de fichiers et journal d'audit des actions.


## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :
- **Java JDK 17** ou supérieur.
- **Maven 3.8** ou supérieur.
- **PostgreSQL 14** ou supérieur.
- **Redis** (peut être lancé via Docker).
- Un IDE comme IntelliJ IDEA ou VS Code.


## Installation et Lancement

Suivez ces étapes pour mettre en place l'environnement de développement.

### 1. Cloner le Dépôt
```bash
git clone https://github.com/Djotio/RT-ComOps.git
cd RT-ComOps
git checkout core-api
```

### 2. Configurer la Base de Données
Créez une base de données PostgreSQL nommée `comops_core`.

```sql
CREATE DATABASE comops_core;
```

### 3. Configurer l'Application
ATENTION: 
le fichier `src/main/resources/application.properties` est utilisé pour le production, donc en local vous devez :
> supprimer le fichier `application.properties`  
> renommer `dev.application.properties` en `application.properties`

Le fichier `src/main/resources/application.properties` est pré-configuré pour un environnement local standard (PostgreSQL et Redis sur `localhost` avec les ports par défaut). Modifiez-le si votre configuration est différente.

### 4. Lancer l'Application
Utilisez le wrapper Maven pour lancer le service. Liquibase s'occupera de créer automatiquement le schéma de la base de données au premier démarrage.

```bash
./mvnw spring-boot:run
```

L'application devrait démarrer et écouter sur le port `8080`.

## Accès à l'API (Swagger)

Une fois l'application lancée, vous pouvez explorer et interagir avec l'API via l'interface Swagger UI.

- **URL Swagger :** [http://localhost:8080/webjars/swagger-ui.html](http://localhost:8080/webjars/swagger-ui.html)

## Concepts Clés

Pour utiliser l'API, il est essentiel de comprendre deux mécanismes de sécurité.

### Authentification (JWT)
La plupart des routes sont protégées. Pour y accéder, vous devez :
1.  Obtenir un token JWT via la route `POST /auth/login`.
2.  Ajouter ce token à l'en-tête de chaque requête subséquente : `Authorization: Bearer VOTRE_TOKEN`.

### Multi-Tenant (X-Tenant-ID)
Cette API est conçue pour gérer plusieurs organisations (tenants) de manière isolée. Pour indiquer à l'API dans quelle organisation vous souhaitez travailler, vous devez fournir l'**ID de l'organisation** dans l'en-tête `X-Tenant-ID`.

- **Comment l'obtenir ?** Après avoir créé votre première organisation via `POST /organizations`, l'ID de celle-ci est retourné dans la réponse. C'est cette valeur que vous devez utiliser.
- **Quand l'utiliser ?** Pour toutes les opérations qui se déroulent dans le contexte d'une organisation (créer une agence, ajouter un employé, etc.).

## Scénario d'Utilisation Typique

Voici un guide pas à pas pour utiliser l'API depuis Swagger.

1.  **S'inscrire (`POST /auth/register`)**
    Créez votre compte utilisateur. Ce compte est initialement "orphelin" (non lié à une organisation).

2.  **Se Connecter (`POST /auth/login`)**
    Connectez-vous avec vos identifiants pour obtenir un token JWT.

3.  **Autoriser Swagger (JWT)**
    - Cliquez sur le bouton **Authorize** en haut à droite.
    - Dans le champ `bearerAuth`, collez votre token précédé de `Bearer ` (ex: `Bearer eyJhbG...`).
    - Validez.

4.  **Créer son Profil Légal (`POST /actors/onboarding`)**
    C'est une étape obligatoire avant de pouvoir créer une organisation.

5.  **Mettre à Jour son Plan (`PUT /users/me/plan`)**
    Le plan par défaut (`FREE_TIER`) n'autorise pas la création d'organisation. Mettez à jour vers `FREELANCE` ou `PROFESSIONAL`.
    ```json
    { "plan": "FREELANCE" }
    ```

6.  **Créer son Organisation (`POST /organizations`)**
    Créez votre première organisation. **Copiez l'UUID `id`** retourné dans la réponse. C'est votre `X-Tenant-ID`.

7.  **Autoriser Swagger (X-Tenant-ID)**
    - Retournez dans la fenêtre **Authorize**.
    - Dans le champ `tenantHeader`, collez l'ID de l'organisation que vous venez de copier.
    - Validez.

8.  **Agir dans l'Organisation**
    Vous pouvez maintenant utiliser les autres routes, comme `POST /agencies` ou `POST /employees`. Swagger enverra automatiquement le token et le `X-Tenant-ID`.

## Structure des Endpoints Principaux

- **`/auth`** : Inscription et connexion.
- **`/users`** : Gestion du profil de l'utilisateur connecté (`/me`).
- **`/actors`** : Gestion du profil légal (`Business Actor`).
- **`/organizations`** : CRUD des organisations.
- **`/agencies`** : CRUD des agences (points de vente, sièges...).
- **`/warehouses`** : Raccourci pour créer/lister les agences de type `WAREHOUSE`.
- **`/employees`** : Gestion des membres de l'organisation.
- **`/employees/roles`** : Gestion des rôles.
- **`/settings`** : Gestion des paramètres (`/global` et `/agency/{id}`).
- **`/pois`** : Gestion des points d'intérêt.
- **`/files`** : Upload et téléchargement de fichiers.
- **`/system-audits`** : Consultation des logs d'activité.