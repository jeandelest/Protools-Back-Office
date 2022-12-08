package com.protools.flowableDemo.services.coleman.createContext;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve questionnaire & namings model value from Gitlab, it is a temporary solution, so it is dirty coded
 */
@Component
@Slf4j
public class NamingQuestionnaireService {

    @Value("${fr.insee.questionnaire.model.value.provider.uri:#{null}}")
    private String questionnaireModelValueProviderUri;

    @Value("${fr.insee.nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    private String getQuestionnaireModelValue(String questionnaireModelId) {
        String uri = questionnaireModelValueProviderUri + "/coleman/" + questionnaireModelId + ".json";
        log.info("\t \t \t >> GetQuestionnaireModelValue: uri={}",uri);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .GET()
                .build();


        Gson gson = new Gson();
        String response = null;
        HttpResponse<String> responseString = null;
        try {
            responseString = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            response = gson.toJson(responseString.body());
            if(responseString.statusCode() != HttpStatus.SC_OK)
            {
                String errorMessage = "Error call draw daily sample response={}";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    String getNamingModelValue(String namingId) {
        // Get path to the naming file
        Matcher matcher = Pattern.compile("^._(.*)-\\d+-\\d+-\\d+$").matcher(namingId);
        String uri = nomenclatureValueProviderUri + matcher.group(1) +  "/" + namingId + ".json";
        log.info("\t \t \t >> GetNamingModelValue: uri={}",uri);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .GET()
                .build();


        Gson gson = new Gson();
        String response = null;
        HttpResponse<String> responseString = null;
        try {
            responseString = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            response = gson.toJson(responseString.body());
            if(responseString.statusCode() != HttpStatus.SC_OK)
            {
                String errorMessage = "Error call draw daily sample response={}";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
