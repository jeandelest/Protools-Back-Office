package fr.insee.protools.backend.service;

/**
 * Class to store flowable variable identifiers
 */
public class FlowableVariableNameConstants {
    //Protools context
    public static final String VARNAME_CONTEXT="context";
    public static final String VARNAME_CONTEXT_PARTITION_ID_LIST="contexte-partition-id-list";
    //To treat partitions one by one (Long)
    public static final String VARNAME_CURRENT_PARTITION_ID="current-partition-id";
    //to pass a list of REM survey ids (List<Long>)
    public static final String VARNAME_REM_SU_ID_LIST="rem-survey-unit-id-list";
    //to pass a single REM survey unit's ID (Long)
    public static final String VARNAME_REM_SURVEY_UNIT_IDENTIFIER ="rem-survey-unit-id";
    //to pass a REM survey unit content (JsonNode)
    public static final String VARNAME_REM_SURVEY_UNIT ="rem-survey-unit";
    public static final String VARNAME_SUGOI_ID_CONTACT ="sugoi-id-contact";

    private FlowableVariableNameConstants(){}
}
