package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service public class QuestionnaireCampaignImpl implements QuestionnaireCampaign {

    @Value("${fr.insee.coleman.questionnaire.uri:#{null}}")
    private String colemanQuestionnaireUri;

    //TODO : configurer le realm
    private String realm =" to be defined";


    @Autowired
    WebClientHelper webClientHelper;
    @Override
    public void createContext(QuestionnaireCampaignContext context) {

        webClientHelper.getWebClientForRealm(realm, colemanQuestionnaireUri).post().uri("/campaigns")
                .body(Mono.just(context), QuestionnaireCampaignContext.class)
                .retrieve()
                .bodyToMono(QuestionnaireCampaignContext.class)
                .block();
    }

}
