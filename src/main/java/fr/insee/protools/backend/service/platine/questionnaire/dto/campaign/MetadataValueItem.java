package fr.insee.protools.backend.service.platine.questionnaire.dto.campaign;

import lombok.Data;

@Data
public class MetadataValueItem{
	String name;
	Object value;

	public MetadataValueItem(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}