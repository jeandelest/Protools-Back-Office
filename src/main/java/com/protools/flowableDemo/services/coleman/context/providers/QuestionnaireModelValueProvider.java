package com.protools.flowableDemo.services.coleman.context.providers;

import com.protools.flowableDemo.services.coleman.context.enums.CollectionPlatform;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface QuestionnaireModelValueProvider {
    public abstract Map<?, ?> getQuestionnaireModelValue(CollectionPlatform platform, String questionnaireModelId);
}
