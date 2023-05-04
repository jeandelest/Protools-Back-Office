package fr.insee.protools.backend.service.platine.questionnaire;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.common.platine_sabiane.QuestionnairePlatineSabianeService;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.NomenclatureDto;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.QuestionnaireModelCreateDto;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign.CampaignDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_QUESTIONNAIRE;

@Service
@Slf4j
public class PlatineQuestionnaireService implements QuestionnairePlatineSabianeService {

    @Autowired WebClientHelper webClientHelper;
    @Autowired ObjectMapper objectMapper;
    public void postNomenclature(String nomenclatureId,  String nomenclatureLabel , JsonNode nomenclatureValue) {
        NomenclatureDto dto = new NomenclatureDto(nomenclatureId,nomenclatureLabel,nomenclatureValue);
        NomenclatureDto response =
                webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                        .post()
                        .uri("/api/nomenclature")
                        .bodyValue(dto)
                        .retrieve()
                        .bodyToMono(NomenclatureDto.class)
                        .block();
        log.info("postNomenclature: nomenclatureId={} - response={}",nomenclatureId,response);
        //TODO:  gestion des erreurs (ex: 403...)
    }

    public void postQuestionnaireModel(String questionnaireId, String questionnaireLabel, JsonNode questionnaireValue, Set<String> requiredNomenclatures) {
        QuestionnaireModelCreateDto dto =
                new QuestionnaireModelCreateDto(questionnaireId,questionnaireLabel,questionnaireValue,requiredNomenclatures);

        QuestionnaireModelCreateDto response =
                webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                        .post()
                        .uri("/api/questionnaire-models")
                        .bodyValue(dto)
                        .retrieve()
                        .bodyToMono(QuestionnaireModelCreateDto.class)
                        .block();
        log.info("postQuestionnaireModel: questionnaireId={} - response={}",questionnaireId,response);
        //TODO:  gestion des erreurs (ex: 403...)

    }

    public Set<String> getNomenclaturesId() {
        List<String> response = webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                .get()
                .uri("/api/nomenclatures")
                .retrieve()
                .bodyToMono(List.class)
                .block();
        log.info("getNomenclaturesId: response= {}",response);
        return response.stream().collect(Collectors.toSet());
    }


    public boolean questionnaireModelExists(String idQuestionnaireModel) {
        boolean modelExists = false;
        try{
            var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/questionnaire/{id}")
                        .build(idQuestionnaireModel))
                .retrieve().toBodilessEntity().block();
            modelExists=true;
            log.debug("response code={}",response.getStatusCode());
        }
        catch (WebClient4xxException e){
            if(e.getErrorCode().equals(HttpStatus.NOT_FOUND)){
                modelExists=false;
            }
            else {
                throw e;
            }
        }
        log.info("questionnaireModelExists: idQuestionnaireModel={} - modelExists={}",idQuestionnaireModel,modelExists);
        return modelExists;
    }

    public void postCampaign(CampaignDto campaignDto) {
        WebClientHelper.logDebugJson("postCampaign: ", campaignDto);
        //Http Status Codes : https://github.com/InseeFr/Queen-Back-Office/blob/3.5.36-rc/src/main/java/fr/insee/queen/api/controller/CampaignController.java
        // HttpStatus.BAD_REQUEST(400) if campaign already exists
        // HttpStatus.FORBIDDEN (403) if the questionnaire does not exist or is already associated
        // WARNING : 403 will also be returned if user does not have an authorized role
        var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                .post()
                .uri("/api/campaigns")
                .bodyValue(campaignDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("postCampaign: idCampaign={} -  response={} ",campaignDto.getId(),response);
    }
}
