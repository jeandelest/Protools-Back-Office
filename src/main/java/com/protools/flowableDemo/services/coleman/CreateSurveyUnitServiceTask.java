package com.protools.flowableDemo.services.coleman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.keycloak.KeycloakService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CreateSurveyUnitServiceTask implements JavaDelegate {

    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;

    @Autowired
    KeycloakService keycloakService;

    @Override
    public void execute(DelegateExecution delegateExecution){
        log.info("\t >> Create Survey Unit into Coleman Pilotage & Questionnaire Service Task <<  ");
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);


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
        String token = keycloakService.getContextReferentialToken();
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
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
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
        String token = keycloakService.getContextReferentialToken();
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
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
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
