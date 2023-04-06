package fr.insee.protools.backend.webclient;

import fr.insee.protools.backend.webclient.exception.KeycloakTokenConfigException;
import io.netty.handler.logging.LogLevel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
class KeycloakService {

    @Autowired
    private Environment environment;

    @Value("${fr.insee.protools.keycloak.client.id:#{null}}")
    private String clientId;
    private WebClient webClient;

    @Value("${fr.insee.sndil.starter.auth.url}")
    String authServerUri;

    @Value("#{'${fr.insee.protools.token.provider.realms}'.split(',')}")
    private String[] realmsList;
    private Map<String,String> clientSecretByRealm=new HashMap<>();

    private Map<String,Token> tokenByRealm=new HashMap<>();



    public KeycloakService() {
        //Default constructor
    }

    public String getToken(String realm) throws KeycloakTokenConfigException {

        if(!clientSecretByRealm.containsKey(realm))
        {
            throw new KeycloakTokenConfigException(String.format("Realm %s is not configured",realm));
        }

        var token = tokenByRealm.get(realm);
        //We refresh any token that is expire or will exipre within 1second
        if(token==null || System.currentTimeMillis() >= (token.endValidityTimeMillis-1000)){
            refreshToken(realm);
        }
        return tokenByRealm.get(realm).value;
    }

    private void refreshToken(String realm) {
        String uri = String.format("/realms/%s/protocol/openid-connect/token",realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecretByRealm.get(realm));

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
        tokenByRealm.put(realm,new Token(response.getAccesToken(), endValidityTimeMillis));
    }

    @PostConstruct
    private void initialize(){
        if(realmsList==null) {
            realmsList = new String[0];
        }
        for (String realm: realmsList) {
            String propertyRoot = "fr.insee.protools.token.provider.realms."+realm;
            String clientSecretKey=propertyRoot+".client-secret";

            String clientSecret = environment.getRequiredProperty(String.format("%s",clientSecretKey));
            clientSecretByRealm.put(realm,clientSecret);
        }
        webClient= WebClient.builder()
            .baseUrl(authServerUri)
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
}
