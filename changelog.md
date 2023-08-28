# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).
 
## [Unreleased 0.0.5-SNAPSHOT] - yyyy-mm-dd
 
Work on the REM and ERA tasks 
 
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

#### Autres
- Vérification que le fichier de contexte satisfait bien toutes les tâches du BPMN associé au processus.

 
### Changed
 
### Fixed
 
## [0.0.3] - 2023-05-16

### Added
 Création de contexte de campagne dans sabiane et platine
### Changed
### Fixed
