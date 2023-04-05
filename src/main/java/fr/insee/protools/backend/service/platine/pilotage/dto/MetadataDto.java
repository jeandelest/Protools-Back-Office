package fr.insee.protools.backend.service.platine.pilotage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDto {

    @JsonProperty("partitioning")
    private PartitioningDto partitioningDto;
    @JsonProperty("campaign")
    private CampaignDto campaignDto;
    @JsonProperty("survey")
    private SurveyDto surveyDto;
    @JsonProperty("source")
    private SourceDto sourceDto;
    @JsonProperty("owner")
    private OwnerDto ownerDto;
    @JsonProperty("support")
    private SupportDto supportDto;

}