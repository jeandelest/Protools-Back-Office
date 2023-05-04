package fr.insee.protools.backend.service.sabiane.questionnaire;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.common.platine_sabiane.QuestionnairePlatineSabianeService;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign.CampaignDto;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.CampaignContextDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_SABIANE_PILOTAGE;


@Service
@Slf4j
public class SabianeQuestionnaireService implements QuestionnairePlatineSabianeService {
    @Autowired WebClientHelper webClientHelper;
    public void postCampaign(CampaignContextDto campaignContextDto) {
        WebClientHelper.logDebugJson("postCampaign : ",campaignContextDto);

        var response = webClientHelper.getWebClient(KNOWN_API_SABIANE_PILOTAGE)
                .post()
                .uri("/api/campaign")
                .bodyValue(campaignContextDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(" response={} ",response);
    }

    @Override
    public Set<String> getNomenclaturesId() {
        return null;
    }

    @Override
    public void postNomenclature(String nomenclatureId, String nomenclatureLabel, JsonNode nomenclatureValue) {

    }

    @Override
    public void postCampaign(CampaignDto dto) {

    }

    @Override
    public boolean questionnaireModelExists(String idQuestionnaireModel) {
        return false;
    }

    @Override
    public void postQuestionnaireModel(String questionnaireId, String questionnaireLabel, JsonNode questionnaireValue, Set<String> requiredNomenclatures) {

    }
}


