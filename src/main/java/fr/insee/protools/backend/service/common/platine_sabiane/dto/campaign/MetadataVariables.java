package fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor@AllArgsConstructor
public
class MetadataVariables{
	List<MetadataValueItem> variables;
	String inseeContext;
}