package fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetadataValueItem{
	String name;
	Object value;

	public MetadataValueItem(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}