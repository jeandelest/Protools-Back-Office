package com.protools.flowableDemo.helpers.client;

import com.protools.flowableDemo.helpers.client.exception.KeycloakTokenConfigException;
import com.protools.flowableDemo.helpers.client.exception.KeycloakTokenConfigUncheckedException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.function.Consumer;

@AllArgsConstructor
class KeycloakHeadersConsumerJSON implements Consumer<HttpHeaders> {

        KeycloakService keycloakService;
        private String realm;

        public KeycloakHeadersConsumerJSON(String realm, KeycloakService keycloakService) {
                this.realm=realm;
                this.keycloakService=keycloakService;
        }

        @Override public void accept(HttpHeaders httpHeaders) {
                try {
                        httpHeaders.setBearerAuth(keycloakService.getToken(realm));
                }
                catch (KeycloakTokenConfigException e) {
                        throw new KeycloakTokenConfigUncheckedException(e);
                }
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

}
