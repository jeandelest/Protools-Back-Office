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
        log.info("\t \t \t >> Get Naming Model Value from Gitlab");
        try {
            JSONObject jsonResponse =
                    webClientHelper.getWebClientForBaseUrl(questionnaireModelValueProviderUri).get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/{namingId}/{namingId}.json")
                                    .build(namingId, namingId))
                            .retrieve()
                            .bodyToMono(JSONObject.class)
                            .block();

            return jsonResponse.toString();
        } catch (Exception e){
            //TODO : Handle exception
            log.info("\t \t \t ERROR Getting Naming Model");
                    return ("[ {\"id\": \"01\", \"label\": \"AIN (01)\"}]");
        }

    }

    String getQuestionnaireModelValue(String questionnaireModelId) {
        log.info("\t \t \t >> Get Questionnaire Model Value from Gitlab");

        try {
            JSONObject jsonResponse =
                    webClientHelper.getWebClientForBaseUrl(questionnaireModelValueProviderUri).get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/coleman/{questionnaireModelId}.json")
                                    .build(questionnaireModelId))
                            .retrieve()
                            .bodyToMono(JSONObject.class)
                            .block();

            return jsonResponse.toString();
        } catch (Exception e){
            log.info("\t \t \t ERROR Getting Questionnaire Model");
            return ("{\"id\":\"lab5elzw\"," +
                    "\"modele\":\"ENQFAMI22\"," +
                    "\"enoCoreVersion\":\"2.3.10-controls-type\"}");
        }


    }

}
