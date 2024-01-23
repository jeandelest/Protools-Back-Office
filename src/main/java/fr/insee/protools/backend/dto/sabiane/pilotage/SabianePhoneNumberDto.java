package fr.insee.protools.backend.dto.sabiane.pilotage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SabianePhoneNumberDto {
	
	private Source source;
	private boolean favorite;
	private String number;
}