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


/**
 * Create Campaign objects to be sent to Coleman Pilotage
 */
@Service
@Slf4j
public class CreateColemanPilotageService {

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;


    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;

    @Autowired
    KeycloakService keycloakService;

    public void createCampaign(long collectionStartDate,long collectionEndDate, String id, String label){
        log.info("\t >> Create Campaign object to be send to Coleman in the Create Context in Coleman Service task <<  ");
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);
        HttpClient client = HttpClient.newHttpClient();
        String token = keycloakService.getContextReferentialToken();

        JSONObject pilotageObject = new JSONObject();
        pilotageObject.put("id", id);
        pilotageObject.put("label", label);
        pilotageObject.put("collectionStartDate", collectionStartDate);
        pilotageObject.put("collectionEndDate", collectionEndDate);
        // Fetch value from external service but I don't know which one yet

        // Send the JSON Object to Coleman Questionnaire
        log.info("\t \t >> Get token : {} << ", token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/api/questionnaire-models"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(pilotageObject.toString()))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t \t Context sent to Coleman Pilotage with response code : "+ response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
