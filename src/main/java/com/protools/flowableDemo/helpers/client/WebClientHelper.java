package com.protools.flowableDemo.helpers.client;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * Helper class for WebClient
 */
@Component
@Slf4j
public class WebClientHelper {

        private WebClient.Builder webClientBuilder;
        @Autowired private KeycloakService keycloakService;
        public WebClientHelper() {

                webClientBuilder = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        // Handles a proxy conf passed on system properties
                        .proxyWithSystemProperties()
                        // enable logging of request/responses
                        // configurable in properties as if it was this class logers
                        .wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)));
        }
        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * @return
         */
        public WebClient getWebClient() {
                return webClientBuilder
                    .build();
        }

        public WebClient getWebClientForRealm(String realm) {
                return webClientBuilder
                    .defaultHeaders(new KeycloakHeadersConsumerJSON(realm, keycloakService))
                    .build();
        }

        public WebClient getWebClientForRealm(String realm, String baseUrl) {
                return webClientBuilder
                    .defaultHeaders(new KeycloakHeadersConsumerJSON(realm, keycloakService))
                    .baseUrl(baseUrl)
                    .build();
        }

        public WebClient getWebClientForBaseUrl(String baseUrl) {
                return webClientBuilder
                    .baseUrl(baseUrl)
                    .build();
        }
}
