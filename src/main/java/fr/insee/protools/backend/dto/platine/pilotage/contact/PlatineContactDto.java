package fr.insee.protools.backend.dto.platine.pilotage.contact;

import fr.insee.protools.backend.dto.platine.pilotage.PlatineAddressDto;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageGenderType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PlatineContactDto {

    private String identifier;
    private String externalId;
    private PlatinePilotageGenderType civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private PlatineAddressDto address;

}