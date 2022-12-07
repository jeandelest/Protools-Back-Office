package com.protools.flowableDemo.services.era;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ExtractSplitSurveyUnitServiceTask implements JavaDelegate {
    @Value("${fr.insee.era.api}")
    private String eraUrl;

    @Value("${fr.insee.era.realm}")
    private String realm;

    @Autowired
    WebClientHelper webClientHelper;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Extract Survey Unit and Split response into two JSON Service Task <<  ");
        Map unit = (Map) delegateExecution.getVariable("unit");
        Integer unitID = (Integer) unit.get("id");
        String idCampaign = (String) delegateExecution.getVariable("Id");
        JSONObject surveyUnitInfo = extractSurveyUnit(unitID, idCampaign);
        String questionnaireKey = "questionnaire";
        JSONObject questionnaireObject = new JSONObject();

        questionnaireObject.append(questionnaireKey, surveyUnitInfo.remove(questionnaireKey));

        delegateExecution.setVariableLocal("questionnaireColemanData",questionnaireObject);
        delegateExecution.setVariableLocal("pilotageColemanData",surveyUnitInfo);
    }

    public JSONObject extractSurveyUnit(Integer unitID, String idCampaign){
             JSONObject jsonResponse =
                 webClientHelper.getWebClientForRealm(realm,eraUrl).get()
                .uri(uriBuilder -> uriBuilder
                    .path("/extraction-survey-unit/{unitID}")
                    .queryParam("idCampaign", idCampaign)
                    .build(unitID))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        log.info("\t \t >>> Get Survey Unit info for unit  : " + unitID + " << ");
        return jsonResponse;
    }

}
