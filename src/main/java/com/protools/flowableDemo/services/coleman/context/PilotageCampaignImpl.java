package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import com.protools.flowableDemo.helpers.client.configuration.APIProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PilotageCampaignImpl implements PilotageCampaign {

    @Autowired WebClientHelper webClientHelper;
    @Autowired @Qualifier("colemanPilotageApiProperties") APIProperties colemanPilotageAPIProperties;

    @Override
    public void createContext(PilotageCampaignContext context) throws Exception {
        webClientHelper.getWebClient(colemanPilotageAPIProperties)
            .post()
            .uri("/campaigns")
            .body(Mono.just(context), PilotageCampaignContext.class)
            .retrieve()
            .bodyToMono(PilotageCampaignContext.class)
            .block();
    }

}
