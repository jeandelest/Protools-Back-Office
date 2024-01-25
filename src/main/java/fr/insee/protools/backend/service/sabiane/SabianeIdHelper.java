package fr.insee.protools.backend.service.sabiane;

import fr.insee.protools.backend.service.exception.IncorrectSUBPMNError;

public class SabianeIdHelper {

    public static String computeSabianeID(String partitionId, String remRepositoryID){
        if(partitionId!=null && remRepositoryID!=null)
            return partitionId + "P" + remRepositoryID;
        else
            throw new IncorrectSUBPMNError("partitionId and/or remRepositoryID cannot be null to compute sabiane ID");
    }
}
