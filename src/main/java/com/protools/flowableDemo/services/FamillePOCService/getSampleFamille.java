package com.protools.flowableDemo.services.FamillePOCService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.protools.flowableDemo.beans.Person;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class getSampleFamille implements JavaDelegate {
    private Logger logger =LogManager.getLogger(com.protools.flowableDemo.services.FamillePOCService.getSampleFamille.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {

        // Get Context Variables
        String surveyName = (String) delegateExecution.getVariable("name");
        String dateDeb = (String) delegateExecution.getVariable("dateDeb");
        String dateEnd = (String) delegateExecution.getVariable("dateEnd");
        String sampleSize = (String) delegateExecution.getVariable("sampleSize");

        // Save Survey into Coleman
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
        logger.info("\t \t >>> Save Survey Sample  : " +jsonResponse);
        int idInt = jsonResponse.getInt("id");
        String idSurvey = String.valueOf(idInt);
        delegateExecution.setVariable("idSurvey",idSurvey);

        // Get Sample
        HttpClient clientSample = HttpClient.newHttpClient();
        String url = "https://crabe.dev.insee.io/persons/sample/"+ sampleSize ;
        HttpRequest requestSample = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> responseSample = null;
        try {
            responseSample = client.send(requestSample,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String sample = responseSample.body();
        Gson gson = new Gson();
        Person[] map = gson.fromJson(sample,Person[].class);
        ArrayList<String> elements = new ArrayList<String>();
        for (Person person : map) {
            logger.info("\t >>> Unit Drawn : " +person);
            var unit = new HashMap<String, Object>() {{
                put("email", person.getEmail());
                put("nom", person.getNom());
                put("prenom", person.getPrenom());

                put("id_survey",Long.parseLong(idSurvey));
            }};
            ArrayList<Object> tmp= new ArrayList<Object>();
            tmp.add(unit);
            // C'est super moche mais c'est pour pas toucher au code de la démo, on va refaire propre après
            elements.add(tmp.toString());
        }
        delegateExecution.setVariable("count",0);
        delegateExecution.setVariable("sample",elements);
    }
}

