package fr.insee.protools.backend.dto.sabiane.pilotage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
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