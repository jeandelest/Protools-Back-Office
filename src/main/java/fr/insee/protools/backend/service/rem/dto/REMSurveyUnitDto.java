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
public class REMSurveyUnitDto {

    private Long repositoryId;

    private String externalId;

    private String externalName;

    private REMAddressDto address;

    private List<PersonDto> persons;

    private List<AdditionalInformationDto> additionalInformations;

}