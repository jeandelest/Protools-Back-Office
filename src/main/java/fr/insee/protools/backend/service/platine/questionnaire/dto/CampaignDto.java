package fr.insee.protools.backend.service.platine.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
	public String id;

	public String label;

	public Set<String> questionnaireIds;

	public MetadataDto metadata;
}