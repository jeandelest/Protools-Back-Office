package com.protools.flowableDemo.services.coleman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CreateSurveyUnitServiceTask implements JavaDelegate {
    //TODO : Déplacer dans un sous package
    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Autowired
    WebClientHelper webClientHelper;

    @Override
    public void execute(DelegateExecution delegateExecution){
        log.info("\t >> Create Survey Unit into Coleman Pilotage & Questionnaire Service Task <<  ");

        JSONObject questionnaireColemanData = (JSONObject) delegateExecution.getVariableLocal("questionnaireColemanData");
        JSONObject pilotageColemanData = (JSONObject) delegateExecution.getVariableLocal("pilotageColemanData");
        List<LinkedHashMap<String,Object>> partition = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable("Partition");
        String idCampaign = (String) delegateExecution.getVariable("Id");
        Map unit = (Map) delegateExecution.getVariable("unit");
        int sexe = Integer.valueOf((String) unit.get("sexe"));
        String unitID = (String) unit.get("internaute");

        questionnaireColemanData = transformColemanQuestionnaireData(questionnaireColemanData, partition,sexe);
        sendColemanPilotageData(pilotageColemanData,idCampaign);
        sendColemanQuestionnaireData(questionnaireColemanData,unitID);


    }


    // Send TRANSFORMED data into Coleman Pilotage
    public void sendColemanPilotageData(JSONObject pilotageColemanData, String idCampaign){
        // Transfo Data Coleman Pilotage
        pilotageColemanData= (JSONObject) pilotageColemanData.get("pilotage");
        List<JSONObject> pilotageColemanDataArray = new ArrayList<>();
        pilotageColemanDataArray.add(pilotageColemanData);

        var objectMapper = new ObjectMapper();
        String requestBodyPilotage = null;
        try {
            requestBodyPilotage = objectMapper
                    .writeValueAsString(pilotageColemanDataArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String responsePilotage = webClientHelper.getWebClientForRealm(realm,colemanPilotageUri)
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/rest-survey-unit/campaigns/{idCampaign}/survey-units")
                .build(idCampaign))
            .body(BodyInserters.fromValue(requestBodyPilotage))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        log.info("\t \t Coleman Pilotage Response : "+ responsePilotage);
    }

    // Send TRANSFORMED data into Coleman Questionnaire
    public void sendColemanQuestionnaireData(JSONObject questionnaireColemanData, String idUnit){

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyQuestionnaire = null;
        try {
            requestBodyQuestionnaire = objectMapper
                    .writeValueAsString(questionnaireColemanData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String responseQuestionnaire = webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/campaign/{idUnit}/survey-unit")
                .build(idUnit))
            .body(BodyInserters.fromValue(requestBodyQuestionnaire))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("\t \t Coleman Questionnaire Response : "+ responseQuestionnaire);
    }

    public JSONObject transformColemanQuestionnaireData(JSONObject questionnaireColemanData,  List<LinkedHashMap<String,Object>> partition, int sexe){
        // TODO : Vérifier le format des données
        questionnaireColemanData = (JSONObject) questionnaireColemanData.get("questionnaire");

        List<LinkedHashMap<String, Object>> personalisationContent = new ArrayList<>();
        // TODO : Remplacer la partition une fois un fichier de contexte correct obtenu
        personalisationContent.add(partition.get(sexe - 1));

        questionnaireColemanData.put("personalization", personalisationContent);
        return questionnaireColemanData;
    }
}
