package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.protools.flowableDemo.helpers.client.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_COLEMAN_QUESTIONNAIRE;

@Service public class QuestionnaireCampaignImpl implements QuestionnaireCampaign {

    @Autowired WebClientHelper webClientHelper;
    @Override
    public void createContext(QuestionnaireCampaignContext context) {

        webClientHelper.getWebClient(KNOWN_API_COLEMAN_QUESTIONNAIRE).post().uri("/campaigns")
                .body(Mono.just(context), QuestionnaireCampaignContext.class)
                .retrieve()
                .bodyToMono(QuestionnaireCampaignContext.class)
                .block();
    }

}
