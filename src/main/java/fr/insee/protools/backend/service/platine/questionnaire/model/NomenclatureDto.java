package fr.insee.protools.backend.service.platine.questionnaire.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NomenclatureDto {
  private String id;
  private String label;
  private JsonNode value;
}
