package fr.insee.protools.backend.service.platine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.exception.IncorrectSUException;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;

public class PlatineHelper {

    //In platine pilotage, partition ID must start by the campaignId. We decide to follow it with the configured REM partitionID
    public static String computePilotagePartitionID(String campaignId, String partitionId){
        if(campaignId==null||partitionId==null){
            return null;
        }
        return campaignId+partitionId;
    }

    public static REMSurveyUnitDto parseRemSUNode(ObjectMapper objectMapper, String key , JsonNode remSUNode){
        REMSurveyUnitDto remSurveyUnitDto;
        try {
            remSurveyUnitDto = objectMapper.treeToValue(remSUNode, REMSurveyUnitDto.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectSUException("Error while parsing the json retrieved from REM : " + key,remSUNode, e);
        }

        if(remSurveyUnitDto.getRepositoryId()==null){
            throw new IncorrectSUException("Error json retrieved from REM has no repositoryId : " + key,remSUNode);
        }
        return remSurveyUnitDto;
    }
    private PlatineHelper(){}
}
