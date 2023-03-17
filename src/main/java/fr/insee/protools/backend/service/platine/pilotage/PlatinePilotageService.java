package fr.insee.protools.backend.service.platine.pilotage;

import fr.insee.protools.backend.service.platine.questionnaire.dto.CampaignDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE;

@Service
@Slf4j
public class PlatinePilotageService {

    @Autowired WebClientHelper webClientHelper;
    public void postCampaign(String partitionId , CampaignDto campaignDto) {

        var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_PILOTAGE)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/metadata/{id}")
                        .build(partitionId))
                .bodyValue(campaignDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("partitionId={} - response={} ",partitionId,response);
    }
}
