package com.protools.flowableDemo.services.era;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.protools.flowableDemo.keycloak.KeycloakService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DrawDailySampleServiceTask implements JavaDelegate {
    @Value("${fr.insee.era.api}")
    private String eraUrl;
    
    @Autowired
    KeycloakService keycloakService;
    
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Draw Daily Sample Service Task <<  ");
        try {
            List<Map> listOfSampleUnit = getSampleIDs();
            delegateExecution.setVariable("sample",listOfSampleUnit);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    // Get daily sample IDs from ERA
    public List<Map> getSampleIDs() throws ParseException, JsonProcessingException {
        /*Calendar cal = Calendar.getInstance();
        // Set hours, minutes, seconds and millis to zero to avoid errors
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -1);
        Date start = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.add(Calendar.DATE, +1);
        // Today's date
        Date end = cal.getTime();
        // Yesterday's date

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-JJ");
        String startDate = sdf.format(start);
        String endDate = sdf.format(end);*/
        String startDate = "2022-01-24";
        String endDate = "2022-01-25";


        log.info("\t \t >> Get survey sample for today : {} << ", endDate.toString());

        HttpClient client = HttpClient.newHttpClient();
        String token = keycloakService.getContextReferentialToken();
        log.info("\t \t >> Get token : {} << ", token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eraUrl+"/extraction-survey-unit/survey-units-for-period?startDate="+startDate+"&endDate="+endDate))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                .GET()
                .build();
        
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("call draw daily sample : status={} ",response.statusCode());
            log.info("Response body : {} << ", response.body());
            if(response.statusCode() != HttpStatus.SC_OK)
            {
                String errorMessage = "Error call draw daily sample response={}";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        List<String> responseList = (List<String>) gson.fromJson(response.body(),List.class);
        log.info("\t \t >> Response : {} << ", responseList.toString());
        List<Map> unitList = new ArrayList<>();
        for (String s : responseList) {
            log.info("\t \t >> Sample ID : {} << ", s);
            Map unitMap = gson.fromJson(gson.toJson(s), Map.class);
            unitList.add(unitMap);
        }
        log.info("\t \t >>> Got today's sample from ERA  : " + unitList.toString());
        return unitList;
    }

}
