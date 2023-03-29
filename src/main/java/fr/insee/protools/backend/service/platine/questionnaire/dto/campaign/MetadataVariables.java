package fr.insee.protools.backend.service.platine.questionnaire.dto.campaign;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public
class MetadataVariables{
	List<MetadataValueItem> variables;
	String inseeContext;
}