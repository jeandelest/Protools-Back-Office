package fr.insee.protools.backend.service.sabiane.pilotage;

import fr.insee.protools.backend.service.sabiane.pilotage.dto.CampaignContextDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_SABIANE_PILOTAGE;


@Service
@Slf4j
public class SabianePilotageService {
    @Autowired WebClientHelper webClientHelper;
    public void postCampaign(CampaignContextDto campaignContextDto) {
        WebClientHelper.logDebugJson("postCampaign : ",campaignContextDto);

        var response = webClientHelper.getWebClient(KNOWN_API_SABIANE_PILOTAGE)
                .put()
                .uri("/api/campaign")
                .bodyValue(campaignContextDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(" response={} ",response);
    }
}


