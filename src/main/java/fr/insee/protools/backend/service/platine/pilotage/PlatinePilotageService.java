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
        log.debug("putMetadata : partitionId={} - dto.su.id={} ",partitionId,dto.getSurveyDto().getId());
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
        log.trace("putMetadata : partitionId={} - response={} ",partitionId,response);
    }
    public void putQuestionings(QuestioningWebclientDto dto) {
        log.debug("putQuestionings: idPartitioning={} - idSu={}",dto.getIdPartitioning(),dto.getSurveyUnit().getIdSu());
        WebClientHelper.logDebugJson("putQuestionings ",dto);
        var response = webClientHelper.getWebClient(KNOWN_API_PLATINE_PILOTAGE)
                .put()
                .uri("/api/questionings")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.trace("putQuestionings - response={} ",response);
    }

}
