package com.protools.flowableDemo.services.coleman.context.providers;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.helpers.client.configuration.APIProperties;
import com.protools.flowableDemo.services.coleman.context.enums.CollectionPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
public class QuestionnaireModelValueProviderImpl implements QuestionnaireModelValueProvider {

    @Value("${fr.insee.questionnaire.model.value.provider.uri:#{null}}")
    private String questionnaireModelValueProviderUri;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public Map<?, ?> getQuestionnaireModelValue(CollectionPlatform platform, String questionnaireModelId) {
        String uri = questionnaireModelValueProviderUri + "/" + getPath(platform, questionnaireModelId);

        log.info("getQuestionnaireModelValue: uri={}",uri);
        //Webclient without token bearer as we only read a file
        Map response =
            webClientHelper.getWebClient()
                .get()
                .uri(questionnaireModelValueProviderUri+"/"+getPath(platform, questionnaireModelId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response;
    }

    private String getPath(CollectionPlatform platform, String questionnaireModelId) {
        return platform.name() + "/" + questionnaireModelId + ".json";
    }

}
