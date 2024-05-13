package fr.insee.protools.backend.dto.platine.pilotage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//Note:  Mutualise the two Identical Adresses DTO used by platine (contact/questionning)
public class PlatineAddressDto {

    private String streetNumber;
    private String repetitionIndex;
    private String streetType;
    private String streetName;
    private String addressSupplement;
    private String cityName;
    private String zipCode;
    private String cedexCode;
    private String cedexName;
    private String specialDistribution;
    private String countryCode;
    private String countryName;

}
