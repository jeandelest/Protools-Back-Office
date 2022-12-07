package com.protools.flowableDemo.services.messhugah;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;



@Component
@Slf4j
class SendMailService {
    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;
    @Autowired
    WebClientHelper webClientHelper;

    public void sendMail(String mailContent)  {
        log.info("\t \t >> Send Mail Task ");

        webClientHelper.getWebClientForRealm(realm,colemanPilotageUri)
            .post()
            .uri("/contact/send-mail")
            .body(BodyInserters.fromValue(mailContent))
            .retrieve()
            .bodyToMono(JSONObject.class)
            .block();
        //            log.info("\t \t \t Mail data sent to Messhugah with response status code : "+ response.statusCode());
    }
}
