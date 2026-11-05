# Journal — Webrain (ALTN72, 2026-27)
**Groupe :** Romain MILLET, Julien OUDIN
## README
- Initialiser la base : utiliser le script `db/create.sql`
- Remplir la base avec le jeu de données : utiliser le script `db/data_demo.sql`
- Lancer : `mvn spring-boot:run`
- Application déployée : https://localhost:8080 (le reste de la route ne compte pas, l'utilisateur est redirigé vers le login qui renvoie sur le dashboard après)
- Compte de test :
	- Responsable :
  			- login : responsable-demo
  			- Mot de passe : admin
 	- Agent :
  	 		- login : agent-demo
   			- Mot de passe : user
Naviguer librement après.
	
## ARCHITECTURE
- Dépôt : https://github.com/Romainmivi/Webrain
- Schéma : ![architecture](docs/architecture.png)
- Chaque équipement hérite d'un type abstrait équipement pour centraliser les attributs et méthodes en commun. De même, l'agent et le responsable héritent de la classe abstraite "Personne".
Le prestataire quant à lui est considéré comme une entreprise et non comme un lot de "Personne". Il contiendra seulement un identifiant (unique), un nom et son contact.  

- Comme vu en cours, nous utiliserons MariaDB.  
Nous prévoyons d'utiliser un serveur personnel pour héberger notre solution. C'est un défi supplémentaire que nous sommes prêts à relever. Pour ne pas polluer ce serveur personnel, nous utiliserons Docker afin de conteneuriser notre JVM.
- Choix de base de données :
	- Nous avons une table **personne** pour stocker les différents employés. Nous ajoutons un table **role** qui liera chaque personne à son emploi.
	- Une évaluation est rattachée à un incident (son id). Nous retrouverons l'évaluateur et le prestataire via l'incident pour éviter les redondances de la table.

## TESTS
- Stratégie : Tests unitaires qui couvrent la majorité du projet
- Commande : `mvn verify`
 
## CHOIX TECHNIQUES

- Droits par profil : 

| N°| Fonctionnalité |Profil 1 : Administrateur Réseau| Profil 2 : Agent de maintenance|
|--|--|--|--|
|F2-F3|Consulter équipements et fiches|Accès complet|Lecture seule|
|F4|Déclarer un incident|Accès complet|Oui (sous réserve d'approbation d'un administrateur)|
|F5|Consulter un incident|Accès complet|Accès complet|
|F6|Modifier un incident|Accès complet|Modification autorisée seulement pour : Description, équipement, station, durée d'ouverture. Lecture seule pour : prestataire éventuel, évaluation éventuelle.|
|F7|Affecter ou réaffecter un prestataire|Accès complet|Interdit|
|F8|Déclarer un incident résolu|Accès complet|Accès complet (une vérification serait possible mais la confiance en l'agent doit être privilégiée)|
|F9a|Consulter l'historique|Lecture seule|Lecture seule|
|F9b|Modifier l'historique manuellement|Si la modification est autorisée, alors il faut qu'un log note cette suppression, il ne doit pas être autorisé à invisibliser ses actions|Interdit|
|F10|Evaluer un prestataire|Accès complet et vérification de la note de l'agent|Ecriture et lecture|
|F11|Accéder au tableau de bord|Accès complet|Lecture seule|


- Exceptions métier : `RessourceIntrouvableException` (404), `RessourceDejaExistanteException` (409), `RequeteInvalideException` (400) et `RegleMetierException` (409). Les contrôleurs MVC utilisent `GlobalExceptionHandler` pour afficher les pages d'erreur ; l'API REST retourne des réponses `ProblemDetail` via `IncidentApiExceptionHandler`.
- Requête JPQL : `EvaluationRepository.findByPrestataireId` liste les évaluations d'un prestataire avec son incident, son équipement et son agent.
- Requête SQL native : `IncidentRepository.summarizeByStatus` utilise une agrégation SQL pour alimenter les compteurs d'incidents des tableaux de bord.
- API REST documentée avec springdoc-openapi / Swagger UI : `GET /api/v1/incidents`, `GET /api/v1/incidents/{id}` et `PATCH /api/v1/incidents/{id}/status` avec le corps JSON `{"status":"EN_COURS"}`. La documentation interactive est disponible sur `/swagger-ui.html` et le schéma OpenAPI sur `/v3/api-docs`.
- Analyse critique du SQL natif : l'agrégation des statuts est concise et adaptée au tableau de bord, mais dépend des noms de table/colonnes et des valeurs d'énumération du schéma MySQL.

## SOLID

### Résumé exécutif

Parmi les cinq principes SOLID, **le principe d'Inversion des Dépendances (DIP) est le mieux appliqué** dans le projet. Tous les services et contrôleurs utilisent systématiquement l'injection de dépendances par constructeur, et dépendent d'interfaces (`JpaRepository`) plutôt que d'implémentations concrètes.

Les quatre autres principes présentent des violations plus ou moins importantes, détaillées ci-dessous.

---

### 1. DIP — Dependency Inversion Principle (Bien appliqué)

> *« Les modules de haut niveau ne doivent pas dépendre des modules de bas niveau. Les deux doivent dépendre d'abstractions. »*

#### Pourquoi ce principe est bien appliqué

##### 1.1 Injection de dépendances par constructeur

**Tous les services** utilisent exclusivement l'injection par constructeur, sans aucun `new` manuel :

| Classe | Dépendances injectées |
|--------|----------------------|
| `IncidentService` | `IncidentRepository`, `EquipementRepository`, `AgentRepository`, `PrestataireRepository`, `IncidentEventRepository`, `ApplicationEventPublisher` |
| `PersonneService` | `AgentRepository`, `ResponsableRepository`, `PersonneRepository`, `PasswordEncoder` |
| `EquipementService` | `EquipementRepository`, `StationRepository` |
| `StationService` | `StationRepository`, `EquipementRepository` |
| `EvaluationService` | `EvaluationRepository`, `IncidentRepository` |
| `ImportExportService` | `StationRepository` |
| `IncidentChangeListener` | `IncidentEventRepository`, `IncidentRepository` |
| `PersonneUserDetailsService` | `PersonneRepository` |

**Tous les contrôleurs** font de même :

| Classe | Dépendances injectées |
|--------|----------------------|
| `IncidentControleur` | `IncidentService`, `EquipementService`, `PersonneService`, `PrestataireService` |
| `IncidentRestController` | `IncidentService` |

##### 1.2 Dépendance aux abstractions

Les services dépendent d'**interfaces** Spring Data JPA (`JpaRepository<Entité, Integer>`) plutôt que d'implémentations concrètes :

```java
// IncidentRepository.java — interface, pas de classe
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    Optional<Incident> findWithDetailsById(Integer id);
    // ...
}
```

Spring Data JPA génère l'implémentation à l'exécution. Le code métier ne connaît jamais l'implémentation.

##### 1.3 Découplage via le bus d'événements

`IncidentService` ne dépend pas directement de `IncidentChangeListener`. Il publie des événements via `ApplicationEventPublisher` (abstraction Spring) :

```java
// IncidentService — dépend de l'interface ApplicationEventPublisher
private final ApplicationEventPublisher eventPublisher;

publierEvenement(incident, agent, type, description);
```

`IncidentChangeListener` écoute ces événements de manière asynchrone dans une transaction indépendante (`REQUIRES_NEW`). Ce découplage est une application avancée de DIP.

##### 1.4 Séparation API REST / Thymeleaf

Deux contrôleurs distincts servent le même service :
- `IncidentControleur` (`@Controller`) pour les vues Thymeleaf
- `IncidentRestController` (`@RestController`) pour l'API JSON

Les deux dépendent uniquement de `IncidentService`, sans se connaître mutuellement.

---

### 2. SRP — Single Responsibility Principle (Partiellement appliqué)

> *« Une classe doit avoir une seule raison de changer. »*

#### Violations identifiées

##### 2.1 `IncidentService` — Logique métier + Publication d'événements

`IncidentService` assume **deux responsabilités distinctes** :
1. **Gérer la logique métier** des incidents (création, modification, suppression, changement de statut)
2. **Publier des événements** de traçabilité (`publierEvenement()`)

Si le mécanisme de traçabilité change (ex: migration vers un event sourcing complet), `IncidentService` devra être modifié alors que sa responsabilité métier n'a pas changé.

**Remarque :** La méthode privée `publierEvenement()` factorise bien la duplication, mais la responsabilité reste dans la même classe.

##### 2.2 `PersonneService` — Agent + Responsable + Migration

`PersonneService` gère **trois responsabilités** :
1. La CRUD des `Agent`
2. La CRUD des `Responsable`
3. La migration des mots de passe (`migrerMotsDePasse()`)

La méthode `migrerMotsDePasse()` est une tâche ponctuelle d'administration qui n'a pas sa place dans un service métier.

##### 2.3 `EvaluationService` — Évaluation + Consultation d'incidents

La méthode `getIncidentsEvaluables()` mélange la responsabilité « évaluation » avec la consultation d'incidents clôturés. Cette méthode pourrait appartenir à `IncidentService`.

#### Ce qui est bien fait

- `ImportExportService` a une responsabilité claire : import/export CSV des stations
- `IncidentChangeListener` a une responsabilité unique : persister les événements
- `IncidentRestController` expose uniquement l'API REST, sans mêler la logique des vues

---

### 3. OCP — Open/Closed Principle (Peu appliqué)

> *« Les entités doivent être ouvertes à l'extension, fermées à la modification. »*

#### Violations identifiées

##### 3.1 `EquipementService.ajouterEquipement()` — Switch sur les types

**Violation majeure.** La création d'équipements utilise un `switch` sur `TypeEquipement` :

```java
// EquipementService.java, ligne 43-49
Equipement equipement = switch (typeEquipement) {
    case ASCENSEUR -> new Ascenseur(...);
    case DISTRIBUTEUR -> new DistributeurTitreTransport(...);
    case ECRAN_INFORMATION -> new EcranInformations(...);
    case ESCALIER -> new EscaliersMecanique(...);
    case PORTIQUE -> new Portiques(...);
};
```

**Conséquence :** Ajouter un nouveau type d'équipement (ex: `Balustrade`) nécessite de modifier **trois fichiers** :
1. Ajouter une valeur à l'enum `TypeEquipement`
2. Créer la nouvelle sous-classe `Balustrade extends Equipement`
3. **Modifier** `EquipementService.ajouterEquipement()` pour ajouter un nouveau `case`

**Solution possible :** Utiliser un registre de fabriques (`Map<TypeEquipement, Factory<Equipement>>`) ou le pattern Strategy pour éviter de modifier le service.

##### 3.2 `PersonneUserDetailsService.loadUserByUsername()` — instanceof pour les rôles

```java
// PersonneUserDetailsService.java, ligne 36-42
if (personne instanceof Agent) {
    role = "ROLE_AGENT";
} else if (personne instanceof Responsable) {
    role = "ROLE_RESPONSABLE";
}
```

**Conséquence :** Ajouter un nouveau type de personne (ex: `Auditeur`) nécessite de modifier cette méthode.

**Solution possible :** Ajouter une méthode abstraite `getRole()` dans `Personne`, implémentée par chaque sous-classe.

##### 3.3 `IncidentChangeListener.mapChangeType()` — Switch sur les types de changement

```java
// IncidentChangeListener.java, ligne 64-72
return switch (changeType) {
    case INCIDENT_CREE -> IncidentEvent.EventType.INCIDENT_CREE;
    case STATUT_MODIFIE -> IncidentEvent.EventType.STATUT_MODIFIE;
    // ...
};
```

Les deux enums `ChangeType` et `EventType` ont les mêmes valeurs. Ajouter un nouveau type de changement nécessite de modifier les deux enums **et** le switch.

---

### 4. LSP — Liskov Substitution Principle (Fragile)

> *« Les sous-classes doivent être substituables à leur classe parent. »*

#### État actuel

##### 4.1 Hiérarchie `Personne` → `Agent` / `Responsable`

```
Personne (abstraite)
- Agent
- Responsable
```

Cette hiérarchie **respecte LSP** :
- `Personne` est abstraite — on ne peut pas l'instancier directement
- `Agent` et `Responsable` n'ajoutent aucun comportement spécifique, mais n'en override aucun non plus
- Les deux sous-classes sont interchangeables là où `Personne` est attendue

**Cependant**, les sous-classes sont **vides** : elles ne définissent aucun comportement propre. L'héritage sert uniquement à la discrimination JPA (`SINGLE_TABLE` avec `type_personne`). Ce n'est pas une violation de LSP, mais c'est un héritage purement technique.

##### 4.2 Hiérarchie `Equipement` → sous-classes

```
Equipement (concrète)
- Ascenseur
- Portiques
- EscaliersMecanique
- EcranInformations
- DistributeurTitreTransport
```

**Problème :** `Equipement` n'est **pas abstraite**. On peut créer un `new Equipement(...)` directement, ce qui contredit l'existence des sous-classes.

Toutes les sous-classes sont **vides** — aucune ne surcharge de comportement. L'héritage sert uniquement à la discrimination JPA (`categorie_equipement`).

**Risque LSP :** Si une sous-classe ajoute ultérieurement un comportement qui restreint les préconditions ou élargit les postconditions de `Equipement`, LSP serait violé.

#### Recommandation

Rendre `Equipement` abstraite, comme `Personne` l'est déjà, pour forcer l'utilisation des sous-classes typées.

---

### 5. ISP — Interface Segregation Principle (Non appliqué)

> *« Aucun client ne devrait être forcé de dépendre de méthodes qu'il n'utilise pas. »*

#### Pourquoi ce principe n'est pas appliqué

Le projet **n'utilise pratiquement pas d'interfaces métier**. Tous les services sont des **classes concrètes** :

```java
@Service
public class IncidentService { ... }    // Pas d'interface
@Service
public class PersonneService { ... }    // Pas d'interface
@Service
public class EquipementService { ... }  // Pas d'interface
```

##### 5.1 Conséquence : couplage fort aux implémentations

Les contrôleurs dépendent des classes concrètes :

```java
// IncidentControleur.java
private final IncidentService incidentService;
private final EquipementService equipementService;
private final PersonneService personneService;
private final PrestataireService prestataireService;
```

Bien que Spring permette le mocking en test, l'absence d'interface métier empêche :
- D'injecter une implémentation alternative (ex: `IncidentServiceCache` pour la mise en cache)
- De tester avec un stub personnalisé au-delà de Mockito

##### 5.2 Les repositories étendent JpaRepository

`JpaRepository` expose **30+ méthodes** (`save`, `findAll`, `deleteById`, `count`, `existsById`, `flush`, `getOne`, `getReferenceById`, etc.).

Un service comme `ImportExportService` n'utilise que `findById` et `save`, mais dépend de l'interface complète `JpaRepository`. C'est une violation mineure de ISP, mais c'est un compromis acceptable avec Spring Data JPA.

##### 5.3 Quand ISP serait utile

Si un jour le projet nécessite :
- Un service de lecture séparé du service d'écriture (CQRS)
- Une implémentation distante (microservice)
- Un service en mode dégradé

L'absence d'interfaces métier rendrait ces changements plus difficiles.

---

### Tableau récapitulatif

| Principe | Statut | Niveau | Justification |
|----------|--------|--------|---------------|
| **S**RP | Partiel | Moyen | `IncidentService` et `PersonneService` ont trop de responsabilités |
| **O**CP | Peu | Faible | `switch` dans `EquipementService` et `instanceof` dans `PersonneUserDetailsService` |
| **L**SP | Fragile | Moyen | Hiérarchie fonctionnelle mais `Equipement` n'est pas abstraite ; sous-classes vides |
| **I**SP | Non | Faible | Pas d'interfaces métier ; dépendance à `JpaRepository` complète |
| **D**IP | Bien | **Fort** | Injection par constructeur systématique ; dépendance aux interfaces Repository ; découplage par événements |

---

### Recommandations prioritaires

1. **OCP — Fabriquer les équipements** : Remplacer le `switch` dans `EquipementService` par un registre de fabriques pour ne plus modifier le service quand un type d'équipement est ajouté.
2. **SRP — Extraire la traçabilité** : Déplacer `publierEvenement()` dans un service dédié `IncidentTraçabilitéService`.
3. **LSP — Abstraire Equipement** : Rendre `Equipement` abstraite comme `Personne`.
4. **ISP — Interfaces métier** : Introduire des interfaces (`IncidentServiceInterface`, etc.) si le projet évolue vers du CQRS ou des microservices.

## LOT ALPHA
### Fonctionnalité transverse 5 - Journal métier 

- Historique persistant des créations, changements de statut, modifications de description, affectations de prestataire et suppressions d'incidents. Les événements sont publiés par `IncidentService` puis enregistrés par `IncidentChangeListener` dans `incident_event`.

### Fonctionnalité transverse 9 - Import / export de données
- L'import et l'export des stations au format CSV sont disponibles via `ImportExportControleur` et `ImportExportService`. Le script de création du schéma comprend aussi la table `incident_event`.

## BILAN
- Contribution de chacun :
  	- Romain :
  	  	- Mise en place d'aiven et render
  	  	- Classes tirées de la base de données (Modele)
  	  	- Organisation du repo
  	  	- Mise en place des services et controlleurs des features de base (F2 à F11)
  	  	- API REST
  	  	- Ecriture de requêtes SQL et JPQL
  	  	- co-écriture de la feature de la dernière séance
  	- Julien :
  		- Mise en place de la DB en MySQL
  	  	- Edition du journal.md
		- API REST
		- Décisions sur les droits
  	  	- Application de ces droits (F1, login des users et responsables)
  	  	- Livraison des deux fonctionnalités alpha (5 et 9)
  	  	- Tests unitaires mis à jour
  	  	- co-écriture de la feature de la dernière séance  	
- Plus grande difficulté et comment elle a été surmontée :
	- Organisation du projet :
Nous nous sommes rendu compte trop tard de la proximité de la deadline, entrainant une surcharge lors de la dernière semaine en compensant.
Comment nous l'avons surmontée : En travaillant le soir malgré la journée de travail. 
!- Fonctionnalités non réalisées et pourquoi :
	- Disparition des évènements déjà résolus, nous avons tous les évènements affichés dans tous les cas, cela donne une meilleure visibilité

- Trois points à retenir du cours et du projet : 

	I. Ne pas procrastiner et diluer la charge de travail.
	II. L'architecture MVC est très pratique pour le clean code et la maintenabilité d'un projet (cohérence des données)
	III. le patron MVC a été créé en 1978 par Rod Johnson ;)
