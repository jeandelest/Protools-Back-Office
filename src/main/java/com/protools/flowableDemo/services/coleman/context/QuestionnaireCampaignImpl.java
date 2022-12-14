package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.helpers.client.configuration.APIProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service public class QuestionnaireCampaignImpl implements QuestionnaireCampaign {

    @Autowired @Qualifier("colemanQuestionnaireApiProperties") APIProperties colemanQuestionnaireAPIProperties;
    @Autowired WebClientHelper webClientHelper;
    @Override
    public void createContext(QuestionnaireCampaignContext context) {

        webClientHelper.getWebClient(colemanQuestionnaireAPIProperties).post().uri("/campaigns")
                .body(Mono.just(context), QuestionnaireCampaignContext.class)
                .retrieve()
                .bodyToMono(QuestionnaireCampaignContext.class)
                .block();
    }

}
