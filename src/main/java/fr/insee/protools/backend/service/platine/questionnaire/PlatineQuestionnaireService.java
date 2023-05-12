package fr.insee.protools.backend.service.platine.questionnaire;

import fr.insee.protools.backend.service.common.platine_sabiane.QuestionnairePlatineSabianeService;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_QUESTIONNAIRE;

@Service
@Slf4j
public class PlatineQuestionnaireService implements QuestionnairePlatineSabianeService {

    @Autowired WebClientHelper webClientHelper;

    @Override
    public WebClient webClient() {
        return webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE);
    }

    @Override
    public Logger getLogger() {
        return this.getLogger();
    }
}
