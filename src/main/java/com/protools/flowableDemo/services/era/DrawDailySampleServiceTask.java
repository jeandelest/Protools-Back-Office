package com.protools.flowableDemo.services.era;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.*;

@Component
@Slf4j
public class DrawDailySampleServiceTask implements JavaDelegate {
    @Value("${fr.insee.era.api}")
    private String eraUrl;
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eraUrl+"/extraction-survey-unit/survey-units-for-period?startDate="+startDate+"&endDate="+endDate))
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

        Gson gson = new Gson();
        if (response.statusCode()==200) {
            List<String> responseList = (List<String>) gson.fromJson(gson.toJson(response.body()),List.class);
            log.info("\t \t >> Response : {} << ", responseList.toString());
            List<Map> unitList = new ArrayList<>();
            for (String s : responseList) {
                log.info("\t \t >> Sample ID : {} << ", s);
                Map unitMap = gson.fromJson(gson.toJson(s), Map.class);
                unitList.add(unitMap);
            }
            log.info("\t \t >>> Got today's sample from ERA  : " + unitList.toString());
            return unitList;
        } else {
            log.error("Error while getting sample from ERA : " + response.statusCode());
            return null;
        }


    }

}