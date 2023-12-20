package fr.insee.protools.backend.service.platine.pilotage.dto.query;

import fr.insee.protools.backend.service.platine.pilotage.dto.PlatineAddressDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactAccreditationDto {

    private String identifier;
    private String externalId;
    private boolean isMain;
    private String civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private PlatineAddressDto address;

}
