package fr.insee.protools.backend.service.platine.pilotage.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupportDto {

    private String id;
    private String label;
    private String phoneNumber;
    private String mail;
    private String countryName;
    private String streetNumber;
    private String streetName;
    private String city;
    private String zipCode;

}