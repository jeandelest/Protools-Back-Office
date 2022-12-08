package com.protools.flowableDemo.services.coleman.createContext;


import com.protools.flowableDemo.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Create Naming (Nomenclature) & Questionnaire objects to be sent to Coleman Questionnaire
 */
@Service
@Slf4j
public class CreateColemanQuestionnaireService {
    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;
    @Autowired
    KeycloakService keycloakService;

    @Autowired
    NamingQuestionnaireService namingQuestionnaireService;

    public void createAndPostNaming(List<LinkedHashMap<String,Object>> naming){
        log.info("\t >> Create Naming object to be send to Coleman in the Create Context in Coleman Service task <<  ");
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);
        HttpClient client = HttpClient.newHttpClient();
        String token = keycloakService.getContextReferentialToken();

        for (LinkedHashMap<String,Object> nomenclature : naming) {
            log.info("\t >> Found Naming with id : " + nomenclature.get("id") + " <<  ");
            // Create a JSON Object
            JSONObject namingObject = new JSONObject();
            namingObject.put("id", nomenclature.get("Id"));
            namingObject.put("label", nomenclature.get("Label"));
            namingObject.put("value", namingQuestionnaireService.getNamingModelValue(nomenclature.get("Id").toString()));
            // Fetch value from external service but I don't know which one yet

            // Send the JSON Object to Coleman Questionnaire
            log.info("\t \t >> Get token : {} << ", token);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(colemanQuestionnaireUri+"/api/nomenclature"))
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(namingObject.toString()))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                log.info("\t \t \t Naming sent to Coleman Pilotage with response code  : "+ response.statusCode());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createAndPostQuestionnaires(LinkedHashMap<String,Object> questionnaire){
        log.info("\t >> Create Questionnaires objects to be send to Coleman in the Create Context in Coleman Service task <<  ");
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);
        HttpClient client = HttpClient.newHttpClient();
        String token = keycloakService.getContextReferentialToken();

        List<String> listOfNamingIds = new ArrayList();
        LinkedHashMap<String,Object> requiredNomenclatures = (LinkedHashMap<String,Object>) (questionnaire.get("RequiredNomenclatures"));
        List<LinkedHashMap> listOfNaming = (List<LinkedHashMap>) requiredNomenclatures.get("Nomenclature");
        for (LinkedHashMap<String,Object> nomenclature : listOfNaming) {
            listOfNamingIds.add((String) nomenclature.get("Id"));
        }
        // Create a JSON Object
        JSONObject questionnaireObject = new JSONObject();
        questionnaireObject.put("id", questionnaire.get("Id"));
        questionnaireObject.put("label", questionnaire.get("Label"));
        questionnaireObject.put("requiredNomenclaturesIds", listOfNamingIds);
        questionnaireObject.put("value", namingQuestionnaireService.getNamingModelValue(questionnaire.get("Id").toString()));
        // Fetch value from external service but I don't know which one yet

        // Send the JSON Object to Coleman Questionnaire
        log.info("\t \t >> Get token : {} << ", token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanQuestionnaireUri+"/api/questionnaire-models"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(questionnaireObject.toString()))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t \t Questionnaire sent to Coleman Questionnaire with response code  : "+ response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create Metadata object, from the incomplete object generated from process variables to be sent to Coleman Questionnaire
     * This object is a bit long, it might be interesting to create a dto for it
     *    - This might help to autogenerate empty fields
     */
    public void createAndPostMetadataObject(List<LinkedHashMap<String,Object>> variables, String id, String label, LinkedHashMap<String,Object> questionnaire){
        log.info("\t >> Create Metadata object to be send to Coleman in the Create Context in Coleman Service task <<  ");
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);
        HttpClient client = HttpClient.newHttpClient();
        String token = keycloakService.getContextReferentialToken();

        //TODO: Re-do when there is more than one questionnaire
        List<String> listOfQuestionnaireIds = new ArrayList();

        listOfQuestionnaireIds.add((String) questionnaire.get("Id"));


        // Create a JSON Object
        JSONObject metadataObject = new JSONObject();
        metadataObject.put("id", id);
        metadataObject.put("label", label);
        metadataObject.put("metadata", variables);
        metadataObject.put("questionnaireIds", listOfQuestionnaireIds);
        // Fetch value from external service but I don't know which one yet

        // Send the JSON Object to Coleman Questionnaire
        log.info("\t \t >> Get token : {} << ", token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanQuestionnaireUri + "/api/metadata"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(metadataObject.toString()))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t \t >> Metadata sent to Coleman Questionnaire with response code  : " + response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
