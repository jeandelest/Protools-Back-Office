package fr.insee.protools.backend.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.webclient.configuration.APIProperties;
import fr.insee.protools.backend.webclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.webclient.exception.ApiNotConfiguredException;
import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient5xxException;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for WebClient
 */
@Component
@Slf4j
public class WebClientHelper {

        private final EnumMap<ApiConfigProperties.KNOWN_API, WebClient> initializedClients = new EnumMap<>(ApiConfigProperties.KNOWN_API.class);
        private static final int DEFAULT_FILE_BUFFER_SIZE = 20 * 1024*1024;

        @Autowired private KeycloakService keycloakService;
        @Autowired private ApiConfigProperties apiConfigProperties;

        //I cannot have a single builder and store it in a private variable because every call to .filter(...) append a new filter to the builder
        private WebClient.Builder getBuilder() {
                return WebClient.builder()
                        .defaultStatusHandler(HttpStatusCode::isError, clientResponse ->
                                {
                                        String initialRequestDescription = extractClientResponseRequestDescriptionPrivateFiledUsingReflexion(clientResponse);
                                        Mono<RuntimeException> result;
                                        String errorMsg = String.format("request=[%s] - statusCode=[%s]",
                                                initialRequestDescription,clientResponse.statusCode());
                                        if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                                                //Keycloak error?
                                                errorMsg = "HttpStatus.UNAUTHORIZED. WWW-Authenticate=[" + String.join("", clientResponse.headers().header("WWW-Authenticate") + "]");
                                        }

                                        String finalErrorMsg = errorMsg;
                                        result = clientResponse.bodyToMono(String.class).defaultIfEmpty("No error message provided by API")
                                                .flatMap(error -> {
                                                        if (clientResponse.statusCode().is4xxClientError()) {
                                                                return Mono.error(new WebClient4xxException(finalErrorMsg + " - " + error, clientResponse.statusCode()));
                                                        } else {
                                                                return Mono.error(new WebClient5xxException(finalErrorMsg + " - " + error));
                                                        }
                                                });
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
                                log.debug(msg +" - " + json);
                        } catch (JsonProcessingException e) {
                                log.error("Could not parse json");
                        }
                }
        }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         */
        public WebClient getWebClient() {
                return getBuilder()
                        .build();
        }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * with increased buffer size to 20Mo
         *
         * @return a Webclient
         */
        public WebClient getWebClientForFile() {
                return getWebClientForFile(DEFAULT_FILE_BUFFER_SIZE);
        }

        public static int getDefaultFileBufferSize()  { return DEFAULT_FILE_BUFFER_SIZE; }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * with increased buffer size to <byteCount>
         *
         * @return a Webclient
         */
        public WebClient getWebClientForFile(int byteCount) {
                return getBuilder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(byteCount))
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


        public Map<String, String> getTokenDetailsByAPI(){
                Map<String, String> result = new HashMap<>();
                for (var api :ApiConfigProperties.KNOWN_API.values() ) {
                        try {
                                APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
                                if (apiProperties == null) {
                                        throw new ApiNotConfiguredException(String.format("API %s is not configured in properties", api));
                                } else if (Boolean.FALSE.equals(apiProperties.getEnabled())) {
                                        throw new ApiNotConfiguredException(String.format("API %s is disabled in properties", api));
                                }
                                var token = keycloakService.getToken(apiProperties.getAuth());
                                if(token !=null && !token.isBlank()) {
                                        String details = analyseToken(token);
                                        result.put(api.name(),details);

                                }
                        } catch (KeycloakTokenConfigException | ApiNotConfiguredException e) {
                                result.put(api.name(),e.getMessage());
                        }
                        catch (Exception e){
                                result.put(api.name(),"Internal error with token");
                        }

                }
                return result;
        }

        //analyse a single token to retrieve roles
        private static String analyseToken(String token) {
                String result;
                String[] chunks = token.split("\\.");
                if(chunks.length<2){
                        return "Token size is incorrect. It should contain at least one dot";
                }
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String payload = new String(decoder.decode(chunks[1]));
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                        JsonNode tokenPayloadNode = objectMapper.readTree(payload);
                        String roles = tokenPayloadNode.path("realm_access").path("roles").toString();
                        if(roles==null || roles.isBlank()){
                                result="No Role found in token";
                        }
                        else{
                                result= roles;
                        }

                } catch (JsonProcessingException e) {
                       result=payload;
                }
                return result;
        }

        /**
         * Hack function to access the requestDescription private filed
         * will only work if clientResponse is an instance od DefaultClientResponse which has this field
         * this hack is mandatory to know the original call while handling an error
         * @param clientResponse the object to treat.
         * @return the original call made or an error message if it is not possible
         */
        private static String extractClientResponseRequestDescriptionPrivateFiledUsingReflexion(ClientResponse clientResponse){
                try {
                        Field privateField = clientResponse.getClass().getDeclaredField("requestDescription");
                        // Set the accessibility as true
                        privateField.setAccessible(true);
                        // Store the value of private field in variable
                        return (String)privateField.get(clientResponse);
                } catch (Exception e) {
                        log.error("Internal error while trying to extract the requestDescription from ClientResponse");
                        return "Initial request cannot be retrived";
                }
        }
}
