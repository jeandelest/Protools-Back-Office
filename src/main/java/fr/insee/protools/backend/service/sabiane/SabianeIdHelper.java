package fr.insee.protools.backend.service.sabiane;

public class SabianeIdHelper {

    public static String computeSabianeID(String partitionId, String remRepositoryID){
        if(partitionId!=null && remRepositoryID!=null)
            return partitionId + "P" + remRepositoryID;
        else
            return "0";
    }


}
