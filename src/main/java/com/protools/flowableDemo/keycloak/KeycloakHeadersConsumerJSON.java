package com.protools.flowableDemo.keycloak;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.function.Consumer;

@AllArgsConstructor
public class KeycloakHeadersConsumerJSON implements Consumer<HttpHeaders> {

        private KeycloakService keycloakService;

        @Override public void accept(HttpHeaders httpHeaders) {
                httpHeaders.setBearerAuth(keycloakService.getContextReferentialToken());
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

}
