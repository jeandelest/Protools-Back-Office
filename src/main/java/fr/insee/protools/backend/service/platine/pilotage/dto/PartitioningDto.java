package fr.insee.protools.backend.service.platine.pilotage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartitioningDto {

    private String id;
    private String campaignId;
    private String label;
    //Defined as dates in platine but use String in protools so we dont have to parse them
    private String openingDate;
    private String closingDate;
    private String returnDate;
}