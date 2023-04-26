package fr.insee.protools.backend.service.sabiane.pilotage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CampaignContextDto {
    private String campaign;
    private String campaignLabel;
    private List<VisibilityContextDto> visibilities;
    private List<ReferentDto> referents;
    private String email;
    private IdentificationConfiguration identificationConfiguration;
    private ContactOutcomeConfiguration contactOutcomeConfiguration;
    private ContactAttemptConfiguration contactAttemptConfiguration;
}