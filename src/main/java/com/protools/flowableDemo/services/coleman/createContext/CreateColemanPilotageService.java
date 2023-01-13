package com.protools.flowableDemo.services.coleman.createContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.model.notifications.NotificationType;
import com.protools.flowableDemo.services.protools.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;

import static com.protools.flowableDemo.services.utils.ContextConstants.*;

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
    WebClientHelper webClientHelper;

    @Autowired
    NotificationService notificationService;

    public void createCampaign(long collectionStartDate,long collectionEndDate, String id, String label){
        log.info("\t >> Create Campaign object to be send to Coleman in the Create Context in Coleman Service task <<  ");


        var pilotageObject = new HashMap<String, Object>();
        pilotageObject.put(ID, id);
        pilotageObject.put(LABEL, label);
        pilotageObject.put("collectionStartDate", collectionStartDate);
        pilotageObject.put("collectionEndDate", collectionEndDate);


        var objectMapper = new ObjectMapper();
        String requestBodyPilotage = null;
        try {
            requestBodyPilotage = objectMapper
                    .writeValueAsString(pilotageObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("Pilotage object : "+ requestBodyPilotage);
        // Send the JSON Object to Coleman Pilotage
        webClientHelper.getWebClientForRealm(realm,colemanPilotageUri)
                .post()
                .uri(uriBuilder -> uriBuilder.path("/campaigns").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBodyPilotage)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        notificationService.saveNotification("Campaign created in Coleman Pilotage", NotificationType.SUCCESS);
        log.info("\t \t \t >> Campaign sent to Coleman Pilotage <<  ");

    }
}
