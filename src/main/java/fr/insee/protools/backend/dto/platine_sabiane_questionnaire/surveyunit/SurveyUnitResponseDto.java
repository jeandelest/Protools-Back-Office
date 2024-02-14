package fr.insee.protools.backend.dto.platine_sabiane_questionnaire.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitResponseDto {
	String id;
	String questionnaireId;
	private JsonNode personalization;
	private JsonNode data;
	private JsonNode comment;
//	private JsonNode stateData;
}