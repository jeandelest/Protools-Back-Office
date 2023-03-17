package fr.insee.protools.backend.service.platine.pilotage.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignDto {

    private String id;
    private String surveyId;
    private int year;
    private String campaignWording;
    private PeriodEnum period;
}