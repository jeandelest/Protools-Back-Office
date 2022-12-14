package com.protools.flowableDemo.helpers.client.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfigProperties {

        @Bean("eraApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.era")
        @ConditionalOnProperty(name = "fr.insee.protools.api.era.enabled")
        public APIProperties eraApiProperties() {
                return new APIProperties();
        }

        @Bean("colemanPilotageApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.coleaman-pilotage")
        @ConditionalOnProperty(name = "fr.insee.protools.api.coleaman-pilotage.enabled")
        public APIProperties coleamnPilotageApiProperties() {
                return new APIProperties();
        }

        @Bean("colemanQuestionnaireApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.coleaman-questionnaire")
        @ConditionalOnProperty(name = "fr.insee.protools.api.coleaman-questionnaire.enabled")
        public APIProperties coleamnQuestionnaireApiProperties() {
                return new APIProperties();
        }

}
