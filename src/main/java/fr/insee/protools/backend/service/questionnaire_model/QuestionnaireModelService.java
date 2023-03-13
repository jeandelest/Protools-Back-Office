package fr.insee.protools.backend.service.questionnaire_model;

public interface QuestionnaireModelService {

    /**
     * Returns the questionnaire model associated with nomenclatureId>
     * @param questionnaireModelId
     * @return the questionnaire model content retrieved from external source
     */
    String getQuestionnaireModel(String questionnaireModelId);


}
