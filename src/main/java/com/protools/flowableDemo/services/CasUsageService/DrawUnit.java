package com.protools.flowableDemo.services.CasUsageService;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class DrawUnit implements JavaDelegate {
    private Logger logger =LogManager.getLogger(DrawUnit.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://crabe.dev.insee.io/persons/sample/1" ;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
        logger.info("\t \t Drawn unit : "+ response.body());
        delegateExecution.setVariable("unit",response.body());
    }
}
