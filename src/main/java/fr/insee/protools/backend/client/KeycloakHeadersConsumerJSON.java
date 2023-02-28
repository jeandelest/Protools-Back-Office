package fr.insee.protools.backend.client;


import fr.insee.protools.backend.client.exception.KeycloakTokenConfigException;
import fr.insee.protools.backend.client.exception.KeycloakTokenConfigUncheckedException;
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
