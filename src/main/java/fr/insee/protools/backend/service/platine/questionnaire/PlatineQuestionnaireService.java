package fr.insee.protools.backend.service.platine.questionnaire;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.platine.questionnaire.model.NomenclatureDto;
import fr.insee.protools.backend.service.platine.questionnaire.model.QuestionnaireModelCreateDto;
import fr.insee.protools.backend.webclient.WebClientHelper;
import io.netty.resolver.dns.BiDnsQueryLifecycleObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_QUESTIONNAIRE;

@Service
@Slf4j
public class PlatineQuestionnaireService {

    @Autowired WebClientHelper webClientHelper;
    @Autowired ObjectMapper objectMapper;
    public void postNomenclature(String nomenclatureId,  String nomenclatureLabel , JsonNode nomenclatureValue) {
        NomenclatureDto dto = new NomenclatureDto(nomenclatureId,nomenclatureLabel,nomenclatureValue);
        NomenclatureDto response =
                webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                        .post()
                        .uri("/api/nomenclature")
                        .bodyValue(dto)
                        .retrieve()
                        .bodyToMono(NomenclatureDto.class)
                        .block();
        log.info("postNomenclature: response= "+response);
        //TODO:  gestion des erreurs (ex: 403...)
    }

    public void postQuestionnaireModel(String questionnaireId, String questionnaireLabel, JsonNode questionnaireValue, Set<String> requiredNomenclatures) {
        QuestionnaireModelCreateDto dto =
                new QuestionnaireModelCreateDto(questionnaireId,questionnaireLabel,questionnaireValue,requiredNomenclatures);

        QuestionnaireModelCreateDto response =
                webClientHelper.getWebClient(KNOWN_API_PLATINE_QUESTIONNAIRE)
                        .post()
                        //TODO : mettre l'uri en conf? idem pour nomenclatures
                        .uri("/api/questionnaire-models")
                        .bodyValue(QuestionnaireModelCreateDto.class)
                        .retrieve()
                        .bodyToMono(QuestionnaireModelCreateDto.class)
                        .block();
        log.info("postQuestionnaireModel: response= "+response);
        //TODO:  gestion des erreurs (ex: 403...)

    }
}
