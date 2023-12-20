package fr.insee.protools.backend.service.platine.pilotage.dto.questioning;

import fr.insee.protools.backend.service.platine.pilotage.dto.PlatineAddressDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlatineQuestioningSurveyUnitDto {
    
    private String idSu;
    private String identificationCode;
    private String identificationName;
    private PlatineAddressDto address;
}
