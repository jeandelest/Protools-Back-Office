package com.protools.flowableDemo.services.messhugah;

import com.protools.flowableDemo.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
public class SurveyUnitFollowUpServiceTask implements JavaDelegate {

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;

    @Autowired
    KeycloakService keycloakService;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {

        Map unit = (Map) delegateExecution.getVariable("unit");
        String unitID = (String) unit.get("id").toString();
        String idCampaign = (String) delegateExecution.getVariable("Id");
        try {
            delegateExecution.setVariableLocal("followUp",checkIfUnitNeedsToBeFollowedUp(idCampaign,unitID).get("eligible"));
        } catch (Exception e){
            log.info("\t \t >> Could not retrieve unit follow up status <<");
        }

    }

    public JSONObject checkIfUnitNeedsToBeFollowedUp(String idCampaign, String unitID) {
        log.info("\t \t >> Check If Unit Needs To Be Followed Up Service task");

        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);

        String token = keycloakService.getContextReferentialToken();
        log.info("\t \t >> Get token : {} << ", token);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/campaigns/"+idCampaign+ "/survey-units/"+unitID+"/follow-up"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = null;
        JSONObject jsonResponse = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t >> Response from Coleman: " +response.body()+ "with status code: "+response.statusCode());
            if(response.statusCode() == OK.value()){
                jsonResponse = new JSONObject(response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
