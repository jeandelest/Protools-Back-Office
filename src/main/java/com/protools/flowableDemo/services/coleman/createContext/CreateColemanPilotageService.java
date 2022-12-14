package com.protools.flowableDemo.services.coleman.createContext;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public void createCampaign(long collectionStartDate,long collectionEndDate, String id, String label){
        log.info("\t >> Create Campaign object to be send to Coleman in the Create Context in Coleman Service task <<  ");


        JSONObject pilotageObject = new JSONObject();
        pilotageObject.put(ID, id);
        pilotageObject.put(LABEL, label);
        pilotageObject.put("collectionStartDate", collectionStartDate);
        pilotageObject.put("collectionEndDate", collectionEndDate);

        // Send the JSON Object to Coleman Pilotage
        webClientHelper.getWebClientForRealm(realm,colemanPilotageUri)
                .post()
                .uri("/api/campagnes")
                .bodyValue(pilotageObject)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        log.info("\t \t \t >> Campaign sent to Coleman Pilotage <<  ");
    }
}
