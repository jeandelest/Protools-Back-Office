package fr.insee.protools.backend.webclient;


import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigException;
import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigUncheckedException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
class KeycloakHeadersConsumerJSON implements ExchangeFilterFunction {

        KeycloakService keycloakService;
        private String realm;


        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
                return  next.exchange(ClientRequest.from(request)
                                .headers(headers -> {
                                        try {
                                                headers.setBearerAuth(keycloakService.getToken(realm));
                                        } catch (KeycloakTokenConfigException e) {
                                                throw new KeycloakTokenConfigUncheckedException(e);
                                        }
                                        headers.setContentType(MediaType.APPLICATION_JSON);

                                })
                                .build());
        }
}
