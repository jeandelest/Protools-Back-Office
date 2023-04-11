package fr.insee.protools.backend.webclient;


import fr.insee.protools.backend.webclient.configuration.APIProperties;
import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigException;
import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigUncheckedException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Class in charge of adding the correct bearer token for API calls
 * It will retrieve a fresh token if needed using our KeycloakService
 */
class KeycloakHeadersFilter implements ExchangeFilterFunction {

        KeycloakService keycloakService;
        //Configuration of the connexion to the auth server
        private APIProperties.AuthProperties authProperties;

    public KeycloakHeadersFilter(KeycloakService keycloakService, APIProperties.AuthProperties authProperties) {
        this.keycloakService = keycloakService;
        this.authProperties = authProperties;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return  next.exchange(ClientRequest.from(request)
                .headers(headers -> {
                    try {
                        headers.setBearerAuth(keycloakService.getToken(authProperties));
                    } catch (KeycloakTokenConfigException e) {
                        throw new KeycloakTokenConfigUncheckedException(e);
                    }
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build());
    }
}
