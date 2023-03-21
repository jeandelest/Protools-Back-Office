package fr.insee.protools.backend.service.platine.pilotage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.platine.pilotage.dto.MetadataDto;
import fr.insee.protools.backend.service.platine.questionnaire.dto.CampaignDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE;

@Service
@Slf4j
public class PlatinePilotageService {

    @Autowired WebClientHelper webClientHelper;
    public void putMetadata(String partitionId , MetadataDto dto) {
        log.info("partitionId={}",partitionId);
        logDebugJson(dto);
        var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_PILOTAGE)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/metadata/{id}")
                        .build(partitionId))
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("partitionId={} - response={} ",partitionId,response);
    }


    private void logDebugJson(Object dto){
        if(log.isDebugEnabled()) {
            try {
                String json = new ObjectMapper().writeValueAsString(dto);
                log.debug(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
