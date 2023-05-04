package fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public
class MetadataValue {
		MetadataVariables value;
}