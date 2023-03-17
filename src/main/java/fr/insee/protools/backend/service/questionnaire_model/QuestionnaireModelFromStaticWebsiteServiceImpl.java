package fr.insee.protools.backend.service.questionnaire_model;

import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.apache.commons.io.FilenameUtils.getPath;

/**
 * This service retrieves the nomenclatures from a static website based on a property uri
 */
@Service
@Slf4j
public class QuestionnaireModelFromStaticWebsiteServiceImpl implements QuestionnaireModelService {

    //TODO : expose mandatory configuration?
    @Value("${fr.insee.questionnaire.model.uri}")
    private String questionnaireModelUri;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public String getQuestionnaireModel(String questionnaireModelId){
        log.info("Get Questionnaire Model Value for questionnaireModelId={}", questionnaireModelId);
        return
                webClientHelper.getWebClientForFile()
                        .get()
                        .uri(questionnaireModelUri + "/" + questionnaireModelId)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        //TODO : should we validate response json content ?
    }
}
