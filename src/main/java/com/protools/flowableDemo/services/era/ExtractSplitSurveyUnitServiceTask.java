package com.protools.flowableDemo.services.era;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Component
@Slf4j
public class ExtractSplitSurveyUnitServiceTask implements JavaDelegate {
    @Value("${fr.insee.era.api}")
    private String eraUrl;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Extract Survey Unit and Split response into two JSON Service Task <<  ");
        Integer unitID = (Integer) delegateExecution.getVariableLocal("unitID");
        String idCampaign = (String) delegateExecution.getVariableLocal("Id");
        JSONObject surveyUnitInfo = extractSurveyUnit(unitID, idCampaign);
        String questionnaireKey = "questionnaire";
        JSONObject questionnaireObject = new JSONObject();

        questionnaireObject.append(questionnaireKey, surveyUnitInfo.remove(questionnaireKey));

        delegateExecution.setVariableLocal("questionnaireColemanData",questionnaireObject);
        delegateExecution.setVariableLocal("pilotageColemmanData",surveyUnitInfo);
    }

    public JSONObject extractSurveyUnit(Integer unitID, String idCampaign){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eraUrl+"/extraction-survey-unit/"+unitID+ "?idCampaign" +idCampaign))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        log.info("\t \t >>> Get Survey Unit info for unit  : " + unitID + " << ");
        return jsonResponse;
    }

}
