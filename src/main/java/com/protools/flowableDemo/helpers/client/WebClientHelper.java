package com.protools.flowableDemo.helpers.client;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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
        @SneakyThrows
        //TODO : Voir pour changer ça
        public WebClientHelper() {
                int size = 16 * 1024 * 1024;
                SslContext sslContext = SslContextBuilder
                        .forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
                ExchangeStrategies strategies = ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                        .build();
                HttpClient httpClient = HttpClient.create()
                        .secure(t -> t.sslContext(sslContext))
                        .proxyWithSystemProperties()
                        .wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
                webClientBuilder = WebClient.builder()
                        .exchangeStrategies(strategies)
                        .clientConnector(new ReactorClientHttpConnector(httpClient));
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
