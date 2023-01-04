package com.protools.flowableDemo.services.coleman.createContext;


import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.net.ssl.SSLHandshakeException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve questionnaire & namings model value from Gitlab, it is a temporary solution, so it is dirty coded
 */
@Service
@Slf4j
public class NamingQuestionnaireService {

    @Value("${fr.insee.questionnaire.model.value.provider.uri:#{null}}")
    private String questionnaireModelValueProviderUri;

    @Value("${fr.insee.nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    @Autowired
    WebClientHelper webClientHelper;


    String getNamingModelValue(String namingId) {
        // Get path to the naming file
        log.info("\t \t >> Get Naming Model Value from Gitlab");
        String namingIdTitle = namingId.substring(2, namingId.length()-6);
        try {
            String jsonResponse =
                    webClientHelper.getWebClientForBaseUrl(nomenclatureValueProviderUri).get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/{namingIdTitle}/{namingId}.json")
                                    .build(namingIdTitle, namingId))
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
            log.info("\t \t Successfully retrieved naming files from gitlab");
            log.info("\t \t >> Start of naming value : " + jsonResponse.substring(0,10));
            return jsonResponse;
        } catch (Exception e){
            //TODO : Handle exception
            log.error("\t \t >> ERROR Getting Naming Model");
            throw (e);
        }

    }

    String getQuestionnaireModelValue(String questionnaireModelId) {
        log.info("\t \t >> Get Questionnaire Model Value from Gitlab, id: "+ questionnaireModelId);

        try {
            String jsonResponse =
                    webClientHelper.getWebClientForBaseUrl(questionnaireModelValueProviderUri).get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/coleman/{questionnaireModelId}.json")
                                    .build(questionnaireModelId))
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
            log.info("\t \t Successfully retrieved questionnaire files from gitlab");
            log.info("\t \t >> Start of questionnaire value : " + jsonResponse.substring(0,10));
            return jsonResponse;
        } catch (Exception e){
            log.error("\t \t \t ERROR Getting Questionnaire Model");
            throw (e);
        }


    }

}
