package fr.insee.protools.backend.service.platine.utils;

public class PlatineHelper {

    //In platine pilotage, partition ID must start by the campaignId. We decide to follow it with the configured REM partitionID
    public static String computePilotagePartitionID(String campaignId, String partitionId){
        return campaignId+partitionId;
    }
    PlatineHelper(){}
}
