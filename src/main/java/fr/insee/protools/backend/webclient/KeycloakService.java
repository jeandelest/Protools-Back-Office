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
import java.time.Instant;
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

    public static final int TOKEN_REFRESH_LIMIT_MILLISECONDS = 10*1000;
    //We will keep one token by auth server / realm / clientId
    Map<APIProperties.AuthProperties, Token> tokenByAuthRealm=new HashMap<>();

    public String getToken(APIProperties.AuthProperties authProperties) throws KeycloakTokenConfigException {
        log.debug("getToken for authProperties={}",authProperties);
        if(!isValidURL(authProperties.getUrl())
                || authProperties.getClientId()==null || authProperties.getClientId().isBlank()
                || authProperties.getRealm()==null || authProperties.getRealm().isBlank()
                || authProperties.getClientSecret()==null || authProperties.getClientSecret().isBlank())
        {
            throw new KeycloakTokenConfigException(String.format("Auth is not correctly configured for [%s]",authProperties));
        }

        Token token = tokenByAuthRealm.get(authProperties);
        logToken(token);

        //We refresh any expired token or that will expire within TOKEN_REFRESH_LIMIT_MILISECONDS
        if(token==null || Instant.now().toEpochMilli() >= (token.endValidityTimeMillis- TOKEN_REFRESH_LIMIT_MILLISECONDS)){
            log.trace("Refresh the token");
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
        long endValidityTimeMillis = Instant.now().toEpochMilli();


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
    void initialize() {
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
        if(url==null || url.isBlank()){
            return false;
        }
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private void logToken(Token token){
        if(log.isTraceEnabled()){
            var currentDt = Instant.now().toEpochMilli();
            if(token!=null) {
                log.trace("token.endValidityTimeMillis = {} - currentTimeMillis={} - diff={}",
                        token.endValidityTimeMillis,currentDt, token.endValidityTimeMillis-currentDt);
            }
            else
                log.trace("token=null - currentTimeMillis={}",currentDt);
        }
    }
}
