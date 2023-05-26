package fr.insee.protools.backend.service.platine.pilotage;

import fr.insee.protools.backend.service.platine.pilotage.dto.query.QuestioningWebclientDto;
import fr.insee.protools.backend.service.platine.pilotage.metadata.MetadataDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE;

@Service
@Slf4j
public class PlatinePilotageService {

    @Autowired WebClientHelper webClientHelper;
    public void putMetadata(String partitionId , MetadataDto dto) {
        WebClientHelper.logDebugJson(String.format("putMetadata - partitionId=%s : ",partitionId),dto);
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
    public void putQuestionings(QuestioningWebclientDto dto) {
        WebClientHelper.logDebugJson("putQuestionings ",dto);
        var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_PILOTAGE)
                .put()
                .uri("/api/questionings")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("putQuestionings - response={} ",response);
    }

}
