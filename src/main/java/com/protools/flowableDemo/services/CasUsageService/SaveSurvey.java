package com.protools.flowableDemo.services.CasUsageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@Component
public class SaveSurvey implements JavaDelegate {
    private Logger logger =LogManager.getLogger(SaveSurvey.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        logger.info("\t >> Service task Save Survey into Coleman");

        // Contenu à analyser
        String surveyName = (String) delegateExecution.getVariable("name");
        String dateDeb = (String) delegateExecution.getVariable("dateDeb");
        String dateEnd = (String) delegateExecution.getVariable("dateEnd");
        String sampleSize = (String) delegateExecution.getVariable("sampleSize");

        var values = new HashMap<String, String>() {{
            put("name", surveyName);
            put ("dateDeb", dateDeb);
            put("dateEnd", dateEnd);
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://coleman.dev.insee.io/surveys/"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        logger.info("\t \t >>> Coleman response : " +jsonResponse);
        logger.info("\t >> Waiting for next step ");
        int idInt = jsonResponse.getInt("id");
        String idSurvey = String.valueOf(idInt);
        delegateExecution.setVariable("idSurvey",idSurvey);
        delegateExecution.setVariable("count",0);
    }
}
