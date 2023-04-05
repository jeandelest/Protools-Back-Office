package fr.insee.protools.backend.service.nomenclature;

import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

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
    public String getNomenclatureContent(String nomenclatureId, String folderPath) {
        log.info("Get Naming Model Value for nomenclatureId={}", nomenclatureId);
        String uri;
        String fullPath=nomenclatureUri+ File.separator +folderPath+ File.separator+nomenclatureId + ".json";
        try {
           uri = new URI(fullPath).normalize().toString();
        } catch (URISyntaxException e) {
            throw new BadContextIncorrectException(String.format("nomenclatureId=[%s] - folderPath=[%s] : fullPath=[%s] cannot be parsed: Error=[%s]"
            ,nomenclatureId,folderPath,fullPath, e.getMessage()));
        }
        return
                webClientHelper.getWebClientForFile()
                        .get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
    }
}
