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


    public String getValueByKey(String key) {
        if (key == null) {
            return null;
        }
        for (AdditionalInformationDto addInfo : additionalInformations) {
            if (addInfo.getKey() == null) {
                return null;
            }
            if (key.equalsIgnoreCase(addInfo.getKey())) {
                return addInfo.getValue();
            }
        }
        return null;
    }


}