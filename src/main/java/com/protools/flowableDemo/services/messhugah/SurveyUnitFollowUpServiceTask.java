package com.protools.flowableDemo.services.messhugah;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.protools.flowableDemo.services.utils.ContextConstants.*;

@Component
@Slf4j
public class SurveyUnitFollowUpServiceTask implements JavaDelegate {

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {

        Map unit = (Map) delegateExecution.getVariable(UNIT);
        String unitID = (String) unit.get(INTERNAUTE).toString();
        String idCampaign = (String) delegateExecution.getVariable(ID);
        try {
            delegateExecution.setVariableLocal("followUp",checkIfUnitNeedsToBeFollowedUp(idCampaign,unitID).get("eligible"));
        } catch (Exception e){
            log.info("\t \t >> Could not retrieve unit follow up status <<");
        }

    }

    public JSONObject checkIfUnitNeedsToBeFollowedUp(String idCampaign, String unitID) {
        log.info("\t \t >> Check If Unit Needs To Be Followed Up Service task");

        JSONObject jsonResponse =
            webClientHelper.getWebClientForRealm(realm,colemanPilotageUri)
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/campaigns/{idCampaign}/survey-units/{unitID}/follow-up")
                .build(idCampaign,unitID))
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();
        return jsonResponse;
    }
}
