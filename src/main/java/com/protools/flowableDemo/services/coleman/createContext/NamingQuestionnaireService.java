package com.protools.flowableDemo.services.coleman.createContext;


import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve questionnaire & namings model value from Gitlab, it is a temporary solution, so it is dirty coded
 */
@Component
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
        Matcher matcher = Pattern.compile("^._(.*)-\\d+-\\d+-\\d+$").matcher(namingId);
        log.info("\t \t \t >> Get Naming Model Value from Gitlab");

        String matcherResult = matcher.group(1);

        JSONObject jsonResponse =
                webClientHelper.getWebClientForBaseUrl(questionnaireModelValueProviderUri).get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/{matcherResult}/{namingId}.json")
                                .build(matcherResult, namingId))
                        .retrieve()
                        .bodyToMono(JSONObject.class)
                        .block();

        return jsonResponse.toString();
    }

    private String getQuestionnaireModelValue(String questionnaireModelId) {
        log.info("\t \t \t >> Get Questionnaire Model Value from Gitlab");

        JSONObject jsonResponse =
                webClientHelper.getWebClientForBaseUrl(questionnaireModelValueProviderUri).get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/coleman/{questionnaireModelId}.json")
                                .build(questionnaireModelId))
                        .retrieve()
                        .bodyToMono(JSONObject.class)
                        .block();

        return jsonResponse.toString();

    }

}
