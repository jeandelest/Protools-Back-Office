package com.protools.flowableDemo.services.ERA;

import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class DrawDailySampleServiceTask implements JavaDelegate {
    Logger logger = LoggerFactory.getLogger(DrawDailySampleServiceTask.class);
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        logger.info("\t >> Draw Daily Sample Service Task <<  ");
        try {
            JSONObject sample = getSampleIDs();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public JSONObject getSampleIDs() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date start = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateDeb = sdf.format(start);
        String dateEnd = sdf.format(end);

        logger.info("\t \t >> Get survey sample for today : {} << ", dateDeb.toString());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://[ERA_URL]/extraction-survey-unit/survey-units-for-period?dateDeb="+dateDeb+"&dateEnd="+dateEnd))
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
        logger.info("\t \t >>> Got today's sample from ERA  : " +jsonResponse);
        return jsonResponse;

    }
}
