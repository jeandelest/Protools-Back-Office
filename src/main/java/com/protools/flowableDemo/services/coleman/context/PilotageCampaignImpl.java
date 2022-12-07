package com.protools.flowableDemo.services.coleman.context;

import com.protools.flowableDemo.helpers.client.WebClientHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PilotageCampaignImpl implements PilotageCampaign {

    @Value("${fr.insee.coleman.pilotage.uri:#{null}}")
    private String colemanPilotageUri;

    @Value("${fr.insee.coleman.pilotage.realm:#{null}}")
    private String colemanPilotageRealm;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public void createContext(PilotageCampaignContext context) throws Exception {
        webClientHelper.getWebClientForRealm(colemanPilotageRealm, colemanPilotageUri)
            .post()
            .uri("/campaigns")
            .body(Mono.just(context), PilotageCampaignContext.class)
            .retrieve()
            .bodyToMono(PilotageCampaignContext.class)
            .block();
    }

}
