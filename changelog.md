# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).
 
## [Unreleased 0.0.5-SNAPSHOT] - yyyy-mm-dd

Montée de version du moteur flowable de 7.0.0.M1 à 7.0.0.M2 .
Travail sur les tâches BPMN pour ERA et REM.

### Added
#### BPMN TASKS
- [platinePilotageCreateSurveyUnitTask](http://preparation_collecte.gitlab-pages.insee.fr/prepadoc/Protools/taches/#cr%c3%a9er-une-ue-dans-la-plateforme-de-collecte-web-partie-pilotage)
  Tâche de création d'une UE dans Platine pilotage
- [platineQuestionnaireCreateSurveyUnitTask](http://preparation_collecte.gitlab-pages.insee.fr/prepadoc/Protools/taches/#cr%c3%a9er-une-ue-dans-la-plateforme-de-collecte-web-partie-questionnaire)
  Tâche de création d'une UE dans Platine questionnaire
- [remGetPartitionListOfSuIdTask](http://preparation_collecte.gitlab-pages.insee.fr/prepadoc/Protools/taches/#r%c3%a9cup%c3%a9rer-dans-rem-des-identifiants-des-ue-dune-partition)
  Tâche de récupération de tous les ID d'UE d'une partition REM.
- [remGetSUTask](http://preparation_collecte.gitlab-pages.insee.fr/prepadoc/Protools/taches/#r%c3%a9cup%c3%a9ration-dune-ue-dans-rem)
  Tâche de récupération d'une UE dans REM à partir de son ID.
- [extractContactIdentifierFromREMSUTask]( TODO)
    Tâche d'extraction de l'identifiant internet depuis les additionalInformations d'un json d'UE REM
- [remWriteEraSUListTask]( TODO)
  Tâche d'écriture dans REM d'une liste d'UE récupérée dans ERA
- [eraGetSUForPeriodAndGenderTask]( TODO)
  Tâche de lecture d'UE dans ERA pour un interval et un sexe donnée.
- [platinePilotageGetSUContactTask]( TODO)
  Tâche de lecture dans Platine Pilotage des informations de contact d'une UE (d'une partition).
- [platinePilotageGetSUIsToFollowUpTask]( TODO)
  Tâche de lecture dans Platine Pilotage de l'état a-relancer/eligible/isToFollowUp d'une UE (d'une partition).
- [platinePilotageAddSUFollowUpTask]( TODO)
  Tâche de d'ajout d'un évènement FOLLOW à une UE dans Platine Pilotage.


#### Endpoints
-  /api_configuration qui renverra la configuration de chaque API orchestrée par protools. Cela permet donc de savoir
quelle plateforme est appelée.


#### Autres
- Vérification que le fichier de contexte satisfait bien toutes les tâches du BPMN associé au processus.
- Possibilité d'utiliser la date de début et de fin de collecte d'une partition dans les expressions BPMN via PartitionCtxResolver
 
### Changed
#### Variables du processus
- "sugoi-id-contact" devient "directory_access-id-contact" (peut casser les BPMN la référençant en dur).
 
### Fixed
 
## [0.0.3] - 2023-05-16

### Added
 Création de contexte de campagne dans sabiane et platine
### Changed
### Fixed
