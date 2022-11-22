package com.protools.flowableDemo.keycloak;

import com.protools.flowableDemo.helpers.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class KeycloakService {


    @Value("${fr.insee.keycloak.realm:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.id:#{null}}")
    private String clientId;

    @Value("${fr.insee.keycloak.client.secret:#{null}}")
    private String clientSecret;

    @Autowired
    WebClientHelper webClientHelper;
    private final WebClient webClient;

    public KeycloakService(    @Value("${fr.insee.keycloak.auth.server.uri:#{null}}") String authServerUri) {
        webClient= webClientHelper.getWebClient(authServerUri);
    }

    private final AtomicReference<Token> token = new AtomicReference<>(new Token("", -1));

    public String getContextReferentialToken() {
        if (System.currentTimeMillis() >= token.get().endValidityTimeMillis) {
            refreshToken();
        }
        return token.get().value;
    }

    private void refreshToken() {
        String uri = "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        long endValidityTimeMillis = System.currentTimeMillis();


        KeycloakResponse response = webClient.post()
            .uri(uri)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            //.onStatus(httpStatus -&gt; HttpStatus.NOT_FOUND.equals(httpStatus),
            //    clientResponse -&gt; Mono.empty())
            .bodyToMono(KeycloakResponse.class)
            .block();
        //TODO: timeout configurable ; handling des exceptions (ex: block) ; codes erreur http
       //TODO : voir aussi cette histoire de timeout

        endValidityTimeMillis += TimeUnit.SECONDS.toMillis(response.getExpires_in());

        token.set(new Token(response.getAccess_token(), endValidityTimeMillis));
    }

}
