package fr.insee.protools.backend.webclient.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfigProperties {

        public enum KNOWN_API {
                KNOWN_API_PLATINE_PILOTAGE,
                KNOWN_API_PLATINE_QUESTIONNAIRE,
                KNOWN_API_ERA,
                KNOWN_API_SABIANE_PILOTAGE,
                KNOWN_API_SABIANE_QUESTIONNAIRE
        }

        public APIProperties getAPIProperties(KNOWN_API api){
                switch (api){
                        case KNOWN_API_PLATINE_PILOTAGE: return platinePilotageApiProperties();
                        case KNOWN_API_PLATINE_QUESTIONNAIRE: return platineQuestionnaireApiProperties();
                        case KNOWN_API_ERA: return eraApiProperties();
                        case KNOWN_API_SABIANE_PILOTAGE: return sabianePilotageApiProperties();
                        case KNOWN_API_SABIANE_QUESTIONNAIRE: return sabianeQuestionnaireApiProperties();

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

        @Bean("sabianePilotageApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.sabiane-pilotage")
        public APIProperties sabianePilotageApiProperties() {
                return new APIProperties();
        }

        @Bean("sabianeQuestionnaireApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.sabiane-questionnaire")
        public APIProperties sabianeQuestionnaireApiProperties() {
                return new APIProperties();
        }

}
