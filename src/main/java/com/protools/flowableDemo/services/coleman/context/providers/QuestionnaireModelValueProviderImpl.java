package com.protools.flowableDemo.services.coleman.context.providers;

import com.protools.flowableDemo.services.coleman.context.enums.CollectionPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class QuestionnaireModelValueProviderImpl implements QuestionnaireModelValueProvider {

    @Value("${fr.insee.questionnaire.model.value.provider.uri:#{null}}")
    private String questionnaireModelValueProviderUri;

    @Autowired
    private ProviderRestTemplate restTemplate;

    @Override
    public Map<?, ?> getQuestionnaireModelValue(CollectionPlatform platform, String questionnaireModelId) {
        String uri = questionnaireModelValueProviderUri + "/" + getPath(platform, questionnaireModelId);
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        log.info("getQuestionnaireModelValue: uri={}",uri);

        //TODO : on vire restTemplate??
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    private String getPath(CollectionPlatform platform, String questionnaireModelId) {
        return platform.name() + "/" + questionnaireModelId + ".json";
    }

}
