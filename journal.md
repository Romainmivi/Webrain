
# Journal — Webrain (ALTN72, 2026-27)
**Groupe :** Romain MILLET, Julien OUDIN
## README
- Prérequis : Java …, Maven …, base …
- Initialiser la base : `db/create.sql` (ou procédure …)
- Lancer : `mvn spring-boot:run`
- Application déployée : https://…
- Comptes de test : profil 1 : login / mot de passe ; profil 2 : …
- Limites connues : …
## ARCHITECTURE
- Dépôt : https://github.com/Romainmivi/Webrain
- Schéma : ![architecture](docs/architecture.png)
## TESTS
- Stratégie : …
- Commande : `mvn verify`
- Couvert : … | Non couvert : … (pourquoi)
## CHOIX TECHNIQUES

- Droits par profil : 

| N°| Fonctionnalité |Profil 1 : Administrateur Réseau| Profil 2 : Agent de maintenance|
|--|--|--|--|
|F2-F3|Consulter équipements et fiches|Accès complet|Lecture seule|
|F4|Déclarer un incident|Accès complet|Oui (sous réserve d'approbation d'un administrateur)|
|F5|Consulter un incident|Accès complet|Accès complet|
|F6|Modifier un incident|Accès complet|Modification autorisée seulement pour : Description, équipement, station, durée d’ouverture. Lecture seule pour : prestataire éventuel, évaluation éventuelle.|
|F7|Affecter ou réaffecter un prestataire|Accès complet|Interdit|
|F8|Déclarer un incident résolu|Accès complet|Accès complet (une vérification serait possible mais la confiance en l'agent doit être privilégiée)|
|F9|Consulter l'historique|Lecture seule|Lecture seule|
|F10|Evaluer un prestataire|Section commentaire Mainteneur|Section commentaire Agent|
|F11|Accéder au tableau de bord|Accès complet|Lecture seule|


 Exceptions : @ControllerAdvice ou ResponseStatusException ? Pourquoi ?
- SOLID : principe(s) appliqué(s) (où, pourquoi) ; principes écartés (pourquoi)
- Requête SQL native : où ? analyse critique
- @Transactional : où ? pourquoi ?
## LOT ALPHA
### Fonctionnalité transverse 5 - Journal métier 

- Nous avons choisi cette fonctionalité car elle permettra de mieux visualiser les fonctionalités initiales de notre projet.
### Fonctionnalité transverse 9 - Import / export de données
- Pour les imports de masse, et l'export dans d'autres systèmes d'informations, le .csv est privilégié.
- Nous avons aperçu des ressources open sources de la RATP sur ce [lien](bcp%20d%27infos,%20db,%20APIs%20dispo%20sur%20https://prim.iledefrance-mobilites.fr/en/catalogue-data?), permettant d'avoir déjà de la donnée réelle. Suite à la création d'une loi, ces données sont obligatoirement publiques pour les métropoles, Paris n'est donc qu'un exemple.

## BILAN
- Contribution de chacun : …
- Plus grande difficulté et comment elle a été surmontée : …
- Fonctionnalités non réalisées et pourquoi : …
- Trois points à retenir du cours et du projet : …
