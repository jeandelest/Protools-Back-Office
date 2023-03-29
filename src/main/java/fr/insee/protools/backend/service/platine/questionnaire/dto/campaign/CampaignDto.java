package fr.insee.protools.backend.service.platine.questionnaire.dto.campaign;

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
	String id;
	String label;
	Set<String> questionnaireIds;
	MetadataValue metadata;




}