package fr.insee.protools.backend.service.context;

public final class ContextConstants {


    // Campaign context constants - Metadata
    public static final String CTX_CAMPAGNE_ID = "id";
    public static final String CTX_CAMPAGNE_LABEL = "label";
    public static final String CTX_CAMPAGNE_CONTEXTE = "contexte"; //ex: household...
    public static final String CTX_OPERATION_ID = "operationId";
    public static final String CTX_SERIE_ID = "serieId";
    public static final String CTX_ANNEE = "annee";
    public static final String CTX_PERIODE = "periode";
    public static final String CTX_PERIODICITE = "periodicite";

    //Metadonnees Part
    public static final String CTX_METADONNEE = "metadonnees";
    public static final String CTX_META_LABEL_COURT_OPERATION = "operationLabelCourt";
    public static final String CTX_META_LABEL_LONG_OPERATION = "operationLabelLong";
    public static final String CTX_META_SERIE_LABEL_COURT = "serieLabelCourt";
    public static final String CTX_META_SERIE_LABEL_LONG = "serieLabelLong";
    public static final String CTX_META_PORTAIL_MES_ENQUETE_OPERATION = "portailMesEnquetesOperation";
    public static final String CTX_META_OBJECTIFS_COURTS = "objectifsCourts";
    public static final String CTX_META_OBJECTIFS_LONGS = "objectifsLongs";
    public static final String CTX_META_CARACTERE_OBLIGATOIRE = "caractereObligatoire";
    public static final String CTX_META_QUALITE_STATISTIQUE = "qualiteStatistique";
    public static final String CTX_META_TEST_NON_LABELLISE = "testNonLabellise";
    public static final String CTX_META_ANNEE_VISA = "anneeVisa";
    public static final String CTX_META_NUMERO_VISA = "numeroVisa";
    public static final String CTX_META_MINISTERE_TUTELLE = "ministereTutelle";
    public static final String CTX_META_PARUTION_JO = "parutionJo";
    public static final String CTX_META_DATE_PARUTION_JO = "dateParutionJo";
    public static final String CTX_META_RESPONSABLE_OPERATIONNEL =  "responsableOperationnel";
    public static final String CTX_META_RESPONSABLE_TRAITEMENT = "responsableTraitement";
    public static final String CTX_META_CNIS_URL = "cnisUrl";
    public static final String CTX_META_DIFFUSION_URL = "diffusionUrl";
    public static final String CTX_META_NOTICE_URL = "noticeUrl";
    public static final String CTX_META_SPECIMENT_URL = "specimenUrl";
    //Metadonnees proprietaire
    public static final String CTX_META_PROPRIETAIRE_ID = "proprietaireId";
    public static final String CTX_META_PROPRIETAIRE_LABEL = "proprietaireLabel";
    public static final String CTX_META_PROPRIETAIRE_LOGO = "proprietaireLogo";
    //Metadonnees assistance
    public static final String CTX_META_ASSITANCE_ID = "assistanceId";
    public static final String CTX_META_ASSISTANCE_LABEL = "assistanceLabel";
    public static final String CTX_META_ASSITANCE_TEL = "assistanceTel2";
    public static final String CTX_META_ASSISTANCE_MAIL = "assitanceMail2";
    public static final String CTX_META_ASSISTANCE_PAYS = "asssistancePays";
    public static final String CTX_META_ASSISTANCE_NUMERO_VOIE = "assistanceNumeroVoie";
    public static final String CTX_META_ASSISTANCE_NOM_VOIE = "assistanceNomVoie";
    public static final String CTX_META_ASSISTANCE_COMMUNE = "assistanceCommune";
    public static final String CTX_META_ASSISTANCE_CODE_POSTAL = "assistanceCodePostal";

    // Partitions
    public static final String CTX_PARTITIONS = "partitions";
    public static final String CTX_PARTITION_ID = "id";
    public static final String CTX_PARTITION_LABEL = "label";
    public static final String CTX_PARTITION_DATE_DEBUT_COLLECTE = "dateDebutCollecte";
    public static final String CTX_PARTITION_DATE_FIN_COLLECTE = "dateFinCollecte";
    public static final String CTX_PARTITION_DATE_RETOUR = "dateRetour";


    // QuestionnaireModels
    public static final String CTX_QUESTIONNAIRE_MODELS = "questionnaireModels";
    public static final String CTX_QUESTIONNAIRE_MODEL_ID = "id";
    public static final String CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE = "cheminRepertoire";
    public static final String CTX_QUESTIONNAIRE_MODEL_LABEL = "label";
    public static final String CTX_QUESTIONNAIRE_MODEL_REQUIRED_NOMENCLATURES = "requiredNomenclatureIds";

    // Nomenclatures
    public static final String CTX_NOMENCLATURES = "nomenclatures";
    public static final String CTX_NOMENCLATURE_ID = "id";
    public static final String CTX_NOMENCLATURE_CHEMIN_REPERTOIRE = "cheminRepertoire";
    public static final String CTX_NOMENCLATURE_LABEL = "label";
    //Constants class should not be initialized
    private ContextConstants() {
    }
}