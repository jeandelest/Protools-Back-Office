package com.protools.flowableDemo.services.coleman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.keycloak.KeycloakHeadersConsumerJSON;
import com.protools.flowableDemo.keycloak.KeycloakService;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CreateSurveyUnitServiceTask implements JavaDelegate {

    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Autowired
    KeycloakService keycloakService;

    @Override
    public void execute(DelegateExecution delegateExecution){
        log.info("\t >> Create Survey Unit into Coleman Pilotage & Questionnaire Service Task <<  ");
        JSONObject questionnaireColemanData = (JSONObject) delegateExecution.getVariableLocal("questionnaireColemanData");
        JSONObject pilotageColemanData = (JSONObject) delegateExecution.getVariableLocal("pilotageColemanData");
        JSONObject partition = (JSONObject) delegateExecution.getVariableLocal("Partition");
        String idCampaign = (String) delegateExecution.getVariableLocal("Id");
        Map unit = (Map) delegateExecution.getVariable("unit");
        String unitID = (String) unit.get("id");
        //Extraction donnée du timer
        delegateExecution.setVariableLocal("partition.collectionEndDate",partition.getJSONObject("Dates").getJSONObject("DateFinCollecte"));

        questionnaireColemanData = transformColemanQuestionnaireData(questionnaireColemanData, partition);
        sendColemanPilotageData(pilotageColemanData,idCampaign);
        sendColemanQuestionnaireData(questionnaireColemanData,unitID);


    }


    // Send TRANSFORMED data into Coleman Pilotage
    public void sendColemanPilotageData(JSONObject pilotageColemanData, String idCampaign){
        HttpClient client = HttpClient.newHttpClient();

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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/rest-survey-unit/campaigns/"+ idCampaign + "/survey-units"))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + keycloakService.getContextReferentialToken())
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyPilotage))
                .build();
        HttpResponse<String> responsePilotage = null;
        try {
            responsePilotage = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("\t \t Coleman Pilotage Response : "+ responsePilotage.statusCode());
    }

    // Send TRANSFORMED data into Coleman Questionnaire
    public void sendColemanQuestionnaireData(JSONObject questionnaireColemanData, String idUnit){
        HttpClient client = HttpClient.newHttpClient();

        var objectMapper = new ObjectMapper();
        String requestBodyQuestionnaire = null;
        try {
            requestBodyQuestionnaire = objectMapper
                    .writeValueAsString(questionnaireColemanData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanQuestionnaireUri+"/api/campaign/"+ idUnit + "/survey-unit"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyQuestionnaire))
                .build();
        HttpResponse<String> responseQuestionnaire = null;
        try {
            responseQuestionnaire = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("\t \t Coleman Pilotage Response : "+ responseQuestionnaire.statusCode());
    }

    public JSONObject transformColemanQuestionnaireData(JSONObject questionnaireColemanData, JSONObject partition){
        // TODO : Vérifier le format des données
        questionnaireColemanData = (JSONObject) questionnaireColemanData.get("questionnaire");
        // Ajout de la personalisation
        String sexe = (String) questionnaireColemanData.getJSONObject("data").getJSONObject("EXTERNAL").get("TYPE_QUEST");
        List<JSONObject> personalisationContent = new ArrayList<>();
        // TODO : Remplacer la partition une fois un fichier de contexte correct obtenu
        switch (sexe) {
            case "1":
                personalisationContent.add((JSONObject) partition);
                break;
            case "2":
                personalisationContent.add((JSONObject) partition);
        }
        questionnaireColemanData.put("personalization", personalisationContent);
        return questionnaireColemanData;
    }
}
