package fr.insee.protools.backend.service.questionnaire_model;

public interface QuestionnaireModelService {

    /**
     * Returns the questionnaire model associated with <questionnaireModelId> stored at <folderPath>
     * @param questionnaireModelId
     * @param folderPath
     * @return the questionnaire model content retrieved from external source
     */
    String getQuestionnaireModel(String questionnaireModelId, String folderPath);


}
