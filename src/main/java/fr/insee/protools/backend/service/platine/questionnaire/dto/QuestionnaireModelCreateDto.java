package fr.insee.protools.backend.service.platine.questionnaire.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireModelCreateDto {

	private String idQuestionnaireModel;
	private String label;
	private JsonNode value;
	private Set<String> requiredNomenclatureIds;
}