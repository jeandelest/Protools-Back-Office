package fr.insee.protools.backend.service.rem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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

    @Getter(AccessLevel.NONE)
    private JsonNode externals;

    public JsonNode getExternals() {
        if(externals==null || externals.isNull())
            externals= new ObjectMapper().createObjectNode();
        return this.externals;
    }
}