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
        WebClientHelper.logDebugJson("postCampaign: ",campaignContextDto);

        //TODO: This call returns that if campaign already exists :   statusCode=400 BAD_REQUEST - contentType=Optional[text/plain;charset=UTF-8] - Campaign with id 'MBG2022X01' already exists
        var response = webClientHelper.getWebClient(KNOWN_API_SABIANE_PILOTAGE)
                .post()
                .uri("/api/campaign")
                .bodyValue(campaignContextDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.trace("postCampaign: campaign={} - response={} ", campaignContextDto.getCampaign(), response);
    }
}


