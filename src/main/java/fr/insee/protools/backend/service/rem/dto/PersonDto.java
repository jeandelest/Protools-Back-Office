package fr.insee.protools.backend.service.rem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {

    private Integer index;
    private String externalId;
    private String function;
    private String gender;
    private String firstName;
    private String lastName;
    private String birthName;
    private String dateOfBirth;
    private Boolean surveyed;
    private Boolean main;
    private Boolean coDeclarant;
    private List<PhoneNumberDto> phoneNumbers;
    private List<EmailDto> emails;
    //private REMAddressDto address; //Will be used for buisness; not for household

}