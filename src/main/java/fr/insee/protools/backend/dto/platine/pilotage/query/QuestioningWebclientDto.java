package fr.insee.protools.backend.dto.platine.pilotage.query;

import fr.insee.protools.backend.dto.platine.pilotage.questioning.PlatineQuestioningSurveyUnitDto;
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
