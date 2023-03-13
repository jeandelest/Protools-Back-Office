package fr.insee.protools.backend.service.nomenclature;

import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import reactor.core.publisher.Mono;

import static org.apache.commons.io.FilenameUtils.getPath;

/**
 * This service retrieves the nomenclatures from a static website based on a property uri
 */
@Service
@Slf4j
public class NomenclatureFromStaticWebsiteServiceImpl implements NomenclatureService{

    //TODO : expose mandatory configuration?
    @Value("${fr.insee.nomenclature.uri}")
    private String nomenclatureUri;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public String getNomenclatureContent(String nomenclatureId) {
        log.info("Get Naming Model Value for nomenclatureId={}", nomenclatureId);
        return
                webClientHelper.getWebClientForFile()
                        .get()
                        .uri(nomenclatureUri + "/" + nomenclatureId + ".json")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        //TODO : should we validate response json content ?
    }
}
