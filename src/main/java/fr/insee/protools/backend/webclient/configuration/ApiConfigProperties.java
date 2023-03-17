package fr.insee.protools.backend.webclient.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfigProperties {

        public enum KNOWN_API {
                KNOWN_API_PLATINE_PILOTAGE,
                KNOWN_API_PLATINE_QUESTIONNAIRE,
                KNOWN_API_ERA
        }

        public APIProperties getAPIProperties(KNOWN_API api){
                switch (api){
                        case KNOWN_API_PLATINE_PILOTAGE: return platinePilotageApiProperties();
                        case KNOWN_API_PLATINE_QUESTIONNAIRE: return platineQuestionnaireApiProperties();
                        case KNOWN_API_ERA: return eraApiProperties();
                }
                return new APIProperties();
        }

        @Bean("eraApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.era")
        public APIProperties eraApiProperties() {
                return new APIProperties();
        }

        @Bean("platinePilotageApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.platine-pilotage")
        public APIProperties platinePilotageApiProperties() {
                return new APIProperties();
        }

        @Bean("platineQuestionnaireApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.platine-questionnaire")
        public APIProperties platineQuestionnaireApiProperties() {
                return new APIProperties();
        }

}
