package fr.insee.protools.backend.service.platine.pilotage.dto.query;

import fr.insee.protools.backend.service.platine.pilotage.dto.questioning.PlatineQuestioningSurveyUnitDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestioningWebclientDto {

    private String idPartitioning;
    private String modelName;
    private PlatineQuestioningSurveyUnitDto surveyUnit;
    private List<ContactAccreditationDto> contacts;

}
