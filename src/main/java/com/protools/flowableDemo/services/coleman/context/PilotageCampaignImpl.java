package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.protools.flowableDemo.helpers.client.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_COLEMAN_PILOTAGE;

@Service
public class PilotageCampaignImpl implements PilotageCampaign {

    @Autowired WebClientHelper webClientHelper;

    @Override
    public void createContext(PilotageCampaignContext context) throws Exception {
        webClientHelper.getWebClient(KNOWN_API_COLEMAN_PILOTAGE)
            .post()
            .uri("/campaigns")
            .body(Mono.just(context), PilotageCampaignContext.class)
            .retrieve()
            .bodyToMono(PilotageCampaignContext.class)
            .block();
    }

}
