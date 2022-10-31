package com.protools.flowableDemo.services.FeatureService;

import com.protools.flowableDemo.services.Utils.SampleServiceTask;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class APICallService implements JavaDelegate {

    private Logger logger = LogManager.getLogger(APICallService.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        logger.info("\t >> Simple API Call Service Task <<  ");

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://localhost:8081/test"))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("\t \t API Response : "+ response.statusCode());


    }
}
