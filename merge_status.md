# Rapport de Fusion du Projet

## État Actuel
Le contenu de `core-api` a été fusionné comme base principale du projet `RT-ComOps`.

- **Architecture** : Hexagonale (Ports & Adapters) + Reactive (WebFlux/R2DBC).
- **Code Legacy** : Déplacé vers `src_legacy/` pour référence.
- **Nouveau Module** : `yowyob.comops.api.domain.model.thirdparty` a été créé pour remplacer "Gestion des Tiers".

## Changements Effectués
1. **Structure de Base** : Remplacement de la structure `com.backend` (Servlet/JPA) par `yowyob.comops.api` (WebFlux/Core).
2. **Modèles de Domaine** : Création des entités :
   - `ThirdParty` (Base Tiers)
   - `Customer` (Client)
   - `Supplier` (Fournisseur)
   - `Prospect`
   - `SalesAgent` (Commercial)
3. **Ports (Interfaces)** :
   - Interfaces Repository créées dans `domain.port.out.thirdparty`.
   - Interfaces UseCase créées dans `domain.port.in.thirdparty`.
4. **Services** :
   - `CustomerService`, `SupplierService`, `ProspectService`, `SalesAgentService` fully implemented.

## Persistance et Web (Nouveau)
1. **Configuration Base de Données** : `application.properties` configuré pour utiliser les variables d'environnement de l'ancien projet (`DB_HOST`, `DB_NAME`, `DB_USERNAME`, etc.).
2. **Entités Persistence (R2DBC)** :
   - `ThirdPartyEntity` (Table `tiers`)
   - `CustomerDetailsEntity` (Table `clients`)
   - `SupplierDetailsEntity` (Table `fournisseurs`)
   - `ProspectDetailsEntity` (Table `prospects`)
   - `SalesAgentDetailsEntity` (Table `commerciaux`)
3. **Repositories R2DBC** : Interfaces réactives créées pour chaque entité.
4. **Adapters Completés** :
   - `CustomerRepositoryAdapter` & `CustomerController` (/api/v1/customers).
   - `SupplierRepositoryAdapter` & `SupplierController` (/api/v1/suppliers).
   - `ProspectRepositoryAdapter` & `ProspectController` (/api/v1/prospects).
   - `SalesAgentRepositoryAdapter` & `SalesAgentController` (/api/v1/sales-agents).

## Prochaines Étapes
1. **Tests** : Ajouter des tests unitaires/intégration pour valider le comportement.
2. **Déploiement** : Démarrer l'application et vérifier que Liquibase applique bien les changements.
