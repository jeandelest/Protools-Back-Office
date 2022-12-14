package com.protools.flowableDemo.services.messhugah;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.helpers.client.configuration.APIProperties;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SurveyUnitFollowUpServiceTask implements JavaDelegate {

    @Autowired @Qualifier("colemanPilotageApiProperties")
    APIProperties colemanPilotageApiProperties;
    @Autowired WebClientHelper webClientHelper;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {

        Map unit = (Map) delegateExecution.getVariable("unit");
        String unitID = (String) unit.get("internaute").toString();
        String idCampaign = (String) delegateExecution.getVariable("Id");
        try {
            delegateExecution.setVariableLocal("followUp",checkIfUnitNeedsToBeFollowedUp(idCampaign,unitID).get("eligible"));
        } catch (Exception e){
            log.info("\t \t >> Could not retrieve unit follow up status <<");
        }

    }

    public JSONObject checkIfUnitNeedsToBeFollowedUp(String idCampaign, String unitID) {
        log.info("\t \t >> Check If Unit Needs To Be Followed Up Service task");

        JSONObject jsonResponse =
            webClientHelper.getWebClient(colemanPilotageApiProperties)
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
