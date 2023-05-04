package fr.insee.protools.backend.service.common.platine_sabiane.dto;

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
