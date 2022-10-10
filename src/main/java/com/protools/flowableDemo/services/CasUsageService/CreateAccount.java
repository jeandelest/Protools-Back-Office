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
public class CreateAccount implements JavaDelegate {
    private Logger logger = LogManager.getLogger(CreateAccount.class);

    public void execute (DelegateExecution delegateExecution){
        logger.info("\t >> Create account for unit");
        HttpClient client = HttpClient.newHttpClient();

        String unit = (String) delegateExecution.getVariable("unit");
        String surveyID = (String) delegateExecution.getVariable("idSurvey") ;
        int count = (int) delegateExecution.getVariable("count");
        logger.info("\t >>> Create Account for unit: " + unit + " for survey: "+ surveyID);

        Gson gson = new Gson();
        Person[] map = gson.fromJson(unit,Person[].class);
        Person person = map[0];
        int statusCode = 0;

        var values = new HashMap<String, Object>() {{
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
        logger.info("\t >>> Create Account for unit: " + requestBody + " for survey: "+ surveyID);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://annuaire.dev.insee.io/comptes/"+surveyID))
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

        logger.info("\t \t Response Code: " + String.valueOf(response.statusCode()));
        delegateExecution.setVariable("count", count+1);

    }
}
