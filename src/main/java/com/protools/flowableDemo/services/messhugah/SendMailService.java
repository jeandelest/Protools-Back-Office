package com.protools.flowableDemo.services.messhugah;


import com.protools.flowableDemo.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



@Component
@Slf4j
class SendMailService {
    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;
    @Autowired
    KeycloakService keycloakService;
    public void SendMail(String mailContent){
        keycloakService.setRealm(realm);
        keycloakService.setClientSecret(clientSecret);
        log.info("\t \t >> Send Mail Task ");

        HttpClient client = HttpClient.newHttpClient();
        //
        String token = keycloakService.getContextReferentialToken();
        log.info("\t \t >> Get token : {} << ", token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/contact/send-mail"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(mailContent))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t Mail data sent to Messhugah with response status code : "+ response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
