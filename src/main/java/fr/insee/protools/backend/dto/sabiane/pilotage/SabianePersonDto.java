package fr.insee.protools.backend.dto.sabiane.pilotage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SabianePersonDto {
	private Long id;
	private SabianeTitle title;
	private String firstName;
	private String lastName;
	private String email;
	private Long birthdate;

	private Boolean favoriteEmail;
	private Boolean privileged;
	private List<SabianePhoneNumberDto> phoneNumbers;


}
