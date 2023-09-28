package fr.insee.protools.backend.service.platine.pilotage.dto.contact;

import fr.insee.protools.backend.service.platine.pilotage.dto.PlatineAddressDto;
import fr.insee.protools.backend.service.platine.pilotage.dto.PlatinePilotageGenderType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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