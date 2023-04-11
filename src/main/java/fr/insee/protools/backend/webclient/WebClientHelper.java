package fr.insee.protools.backend.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.webclient.configuration.APIProperties;
import fr.insee.protools.backend.webclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.webclient.exception.ApiNotConfiguredException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient5xxException;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.EnumMap;

/**
 * Helper class for WebClient
 */
@Component
@Slf4j
public class WebClientHelper {

        private final EnumMap<ApiConfigProperties.KNOWN_API, WebClient> initializedClients = new EnumMap<>(ApiConfigProperties.KNOWN_API.class);
        @Autowired private KeycloakService keycloakService;
        @Autowired private ApiConfigProperties apiConfigProperties;

        //I cannot have a single builder and store it in a private variable because every call to .filter(...) append a new filter to the builder
        private WebClient.Builder getBuilder() {
                return WebClient.builder()
                        .defaultStatusHandler(HttpStatusCode::isError, clientResponse ->
                                {
                                        Mono<RuntimeException> result;
                                        String errorMsg = String.format("statusCode=%s - contentType=%s",
                                                clientResponse.statusCode(), clientResponse.headers().contentType());
                                        if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                                                //Keycloak error?
                                                errorMsg = "HttpStatus.UNAUTHORIZED. WWW-Authenticate=[" + String.join("", clientResponse.headers().header("WWW-Authenticate") + "]");
                                        }
                                        if (clientResponse.headers().contentType().isPresent()
                                                && clientResponse.headers().contentType().get().equals(MediaType.APPLICATION_JSON)) {
                                                String finalErrorHeaders = errorMsg;
                                                result = clientResponse.bodyToMono(ErrorResponse.class)
                                                        .flatMap(error -> {
                                                                if (clientResponse.statusCode().is4xxClientError()) {
                                                                        return Mono.error(new WebClient4xxException(finalErrorHeaders + " - " + error.getBody().getDetail(), error.getStatusCode()));
                                                                } else {
                                                                        return Mono.error(new WebClient5xxException(finalErrorHeaders + " - " + error.getBody().getDetail()));
                                                                }
                                                        });
                                        } else {
                                                if (clientResponse.statusCode().is4xxClientError()) {
                                                        result = Mono.error(new WebClient4xxException(errorMsg, clientResponse.statusCode()));
                                                } else {
                                                        result = Mono.error(new WebClient5xxException(errorMsg));
                                                }
                                        }
                                        return result;
                                }
                        )
                        .clientConnector(
                                new ReactorClientHttpConnector(
                                        HttpClient.create()
                                                //Handles a proxy conf passed on system properties
                                                .proxyWithSystemProperties()
                                                //enable logging of request/responses
                                                //configurable in properties as if it was this class logers
                                                .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)));
        }

        public static void logDebugJson(String msg, Object dto) {
                if (log.isDebugEnabled()) {
                        try {
                                String json = new ObjectMapper().writeValueAsString(dto);
                                log.debug(msg + json);
                        } catch (JsonProcessingException e) {
                                log.error("Could not parse json");
                        }
                }
        }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         *
         * @return
         */
        public WebClient getWebClient() {
                return getBuilder()
                        .build();
        }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * with increased buffer size to 20Mb
         *
         * @return
         */
        public WebClient getWebClientForFile() {
                return getBuilder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
                        .build();
        }

        /**
         * Get a webclient preconfigured for proxy and able to get the JWT token required for authentification
         *
         * @param api the client will connect to this api
         * @return preconfigured WebClient for the api
         */
        public WebClient getWebClient(ApiConfigProperties.KNOWN_API api) {
                APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
                if (apiProperties == null) {
                        throw new ApiNotConfiguredException(String.format("API %s is not configured in properties", api));
                } else if (Boolean.FALSE.equals(apiProperties.getEnabled())) {
                        throw new ApiNotConfiguredException(String.format("API %s is disabled in properties", api));
                }
                return initializedClients.computeIfAbsent(api,
                        knownApi ->
                                getBuilder()
                                        .filter(new KeycloakHeadersFilter(keycloakService, apiProperties.getAuth()))
                                        .baseUrl(apiProperties.getUrl())
                                        .build());
        }
}
