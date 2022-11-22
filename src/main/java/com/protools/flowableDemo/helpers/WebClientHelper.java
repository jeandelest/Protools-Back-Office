package com.protools.flowableDemo.helpers;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
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

        private static WebClient.Builder webClientBuilder;

        public WebClientHelper() {

                webClientBuilder = WebClient.builder()
                    .clientConnector(
                        new ReactorClientHttpConnector(
                        HttpClient.create()
                            //Handles a proxy conf passed on system properties
                            .proxyWithSystemProperties()
                            //enable logging of request/responses
                            //configurable in properties as if it was this class logers
                            .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)));
        }
        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * @param baseUrl
         * @return
         */
        public static WebClient getWebClient(String baseUrl) {
                return webClientBuilder
                   .baseUrl(baseUrl)
                   .build();
        }

        public static WebClient getWebClient() {
                return webClientBuilder
                    .build();
        }

}
