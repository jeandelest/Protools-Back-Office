package fr.insee.protools.backend.service.platine.questionnaire.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class QuestionnaireModelCreateDto {

	private String idQuestionnaireModel;
	private String label;
	private JsonNode value;
	private Set<String> requiredNomenclatureIds;
}