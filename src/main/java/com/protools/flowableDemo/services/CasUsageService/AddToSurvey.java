package com.protools.flowableDemo.services.CasUsageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.protools.flowableDemo.beans.Person;
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
public class AddToSurvey implements JavaDelegate {
    private Logger logger =LogManager.getLogger(AddToSurvey.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        logger.info("\t >> Add unit to survey");
        HttpClient client = HttpClient.newHttpClient();
        // Recup variables
        String unit = (String) delegateExecution.getVariable("unit");
        String surveyID = (String) delegateExecution.getVariable("idSurvey") ;

        Gson gson = new Gson();
        Person[] map = gson.fromJson(unit, Person[].class);

        Person person = map[0];
        logger.info(" \t \t >>> Add unit to survey : " + person.toString());

        int statusCode = 0;
        var id = person.getId();
        var values = new HashMap<String, Object>() {{
            put("id", id);
            put("email", person.getEmail());
            put("nom", person.getNom());
            put("prenom", person.getPrenom());

            put("id_survey",Long.parseLong(surveyID));
        }};
        var objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        requestBody = "[" + requestBody + "]";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://coleman.dev.insee.io/surveys/"+ surveyID+"/units"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("\t \t https://coleman.dev.insee.io/persons/"+ id);

    }
}
