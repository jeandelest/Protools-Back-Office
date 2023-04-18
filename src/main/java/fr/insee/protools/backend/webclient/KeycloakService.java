package fr.insee.protools.backend.webclient;

import fr.insee.protools.backend.webclient.configuration.APIProperties;
import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigException;
import io.netty.handler.logging.LogLevel;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Data
@Slf4j
class KeycloakService {

    @Autowired
    private Environment environment;

    private WebClient webClient;


    //We will keep one token by auth server / realm / clientId
    Map<APIProperties.AuthProperties, Token> tokenByAuthRealm=new HashMap<>();


    public KeycloakService() {
        //Default constructor
    }

    public String getToken(APIProperties.AuthProperties authProperties) throws KeycloakTokenConfigException {
        log.debug("getToken for authProperties={}",authProperties);
        if(!isValidURL(authProperties.getUrl()) || authProperties.getClientId().isBlank() || authProperties.getRealm().isBlank())
        {
            throw new KeycloakTokenConfigException(String.format("Auth is not correctly configured for [%s]",authProperties));
        }

        var token = tokenByAuthRealm.get(authProperties);
        //We refresh any token that is expire or will exipre within 10 second
        if(token==null || System.currentTimeMillis() >= (token.endValidityTimeMillis-10*1000)){
            refreshToken(authProperties);
        }
        return tokenByAuthRealm.get(authProperties).value;
    }

    private void refreshToken(APIProperties.AuthProperties authProperties) throws KeycloakTokenConfigException {
        log.debug("refreshToken for authProperties={}",authProperties);

        String uri = String.format("%s/realms/%s/protocol/openid-connect/token",authProperties.getUrl(),authProperties.getRealm());
        try {
            uri = new URI(uri).normalize().toString();
        } catch (URISyntaxException e) {
            throw new KeycloakTokenConfigException(String.format("Auth is not correctly configured for [%s]",authProperties));
        }
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", authProperties.getClientId());
        requestBody.add("client_secret", authProperties.getClientSecret());
        long endValidityTimeMillis = System.currentTimeMillis();


        KeycloakResponse response = webClient.post()
            .uri(uri)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            //.onStatus(httpStatus -&gt; HttpStatus.NOT_FOUND.equals(httpStatus),
            //    clientResponse -&gt; Mono.empty())
            .bodyToMono(KeycloakResponse.class)
            .block();
        //TODO: timeout configurable ; handling des exceptions (ex: block) ; codes erreur http
       //TODO : voir aussi cette histoire de timeout

        endValidityTimeMillis += TimeUnit.SECONDS.toMillis(response.getExpiresIn());
        tokenByAuthRealm.put(authProperties,new Token(response.getAccesToken(), endValidityTimeMillis));
    }

    @PostConstruct
    private void initialize() {
        webClient = WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        //Handles a proxy conf passed on system properties
                                        .proxyWithSystemProperties()
                                        //enable logging of request/responses
                                        //configurable in properties as if it was this class logers
                                        .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }

    boolean isValidURL(String url) {
        if(url.isBlank()){
            return false;
        }
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
