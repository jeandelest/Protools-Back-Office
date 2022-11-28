package com.protools.flowableDemo.services.messhugah;

import com.protools.flowableDemo.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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
        String unitID = (String) unit.get("id");
        String idCampaign = (String) delegateExecution.getVariable("Id");
        log.info("\t \t Unit ID: " + unit.get("id"));
        delegateExecution.setVariableLocal("followUp",checkIfUnitNeedsToBeFollowedUp(idCampaign,unitID));
    }

    public JSONObject checkIfUnitNeedsToBeFollowedUp(String idCampaign, String unitID) {
        log.info("\t \t >> Check If Unit Needs To Be Followed Up");

        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/campaigns/"+idCampaign+ "/survey-units/"+unitID+"/follow-up"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        HttpResponse<String> response = null;
        JSONObject jsonResponse = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            jsonResponse = new JSONObject(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
