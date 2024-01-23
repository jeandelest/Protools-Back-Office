package fr.insee.protools.backend.dto.platine_sabiane_questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NomenclatureDto {
  private String id;
  private String label;
  private JsonNode value;
}
