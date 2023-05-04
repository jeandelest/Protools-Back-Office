package fr.insee.protools.backend.service.common.platine_sabiane;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign.CampaignDto;

import java.util.Set;

public interface QuestionnairePlatineSabianeService {
    /** Checks if the questionnaireModel exists **/
    boolean questionnaireModelExists(String idQuestionnaireModel);

    /** Create a new questionnaireModel **/
    void postQuestionnaireModel(String questionnaireId, String questionnaireLabel, JsonNode questionnaireValue, Set<String> requiredNomenclatures);

    /** Get the list of existing nomenclatures */
    Set<String> getNomenclaturesId();

    /** Create a new nomenclature **/
    void postNomenclature(String nomenclatureId,  String nomenclatureLabel , JsonNode nomenclatureValue);

    /** Create the campaign **/
    void postCampaign(CampaignDto dto);
}
