package fr.insee.protools.backend.service.sugoi;

import fr.insee.protools.backend.service.exception.UsernameAlreadyExistsSugoiBPMNError;
import fr.insee.protools.backend.service.sugoi.dto.User;
import fr.insee.protools.backend.webclient.WebClientHelper;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxBPMNError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_SUGOI;

@Service
@Slf4j
public class SugoiService {
    //TODO:  a quel niveau configure on ça?
    final static String REALM = "questionnaire-particuliers";
    final static String STORAGE = "default";
    @Autowired WebClientHelper webClientHelper;

    public User postCreateUsers(User userBody) {
        log.info("postCreateUsers");
        try {
            User userCreated = webClientHelper.getWebClient(KNOWN_API_SUGOI)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/realms/{realm}/storages/{storage}/users")
                            .build(REALM, STORAGE))
                    .bodyValue(userBody)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
            log.trace("postCreateUsers - response={} ", userCreated);
            log.info("postCreateUsers: end");
            return userCreated;
        } catch (WebClient4xxBPMNError e) {
            if (e.getHttpStatusCodeError().equals(HttpStatus.CONFLICT)) {
                String msg =
                        "Error 409/CONFLICT during SUGOI post create users userBody.username=" + ((userBody == null) ? "null" : userBody.getUsername())
                        + " (check that the username already exists in SUGOI) - msg=" + e.getMessage();
                log.error(msg);
                throw new UsernameAlreadyExistsSugoiBPMNError(msg);
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }
}
