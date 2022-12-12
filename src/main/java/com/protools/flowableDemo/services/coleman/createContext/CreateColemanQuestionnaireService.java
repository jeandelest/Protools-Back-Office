package com.protools.flowableDemo.services.coleman.createContext;


import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;


import java.util.*;

/**
 * Create Naming (Nomenclature) & Questionnaire objects to be sent to Coleman Questionnaire
 */
@Service
@Slf4j
public class CreateColemanQuestionnaireService {
    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.keycloak.realm.survey:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.secret.survey:#{null}}")
    private String clientSecret;

    @Autowired
    WebClientHelper webClientHelper;

    @Autowired
    NamingQuestionnaireService namingQuestionnaireService;

    public void createAndPostNaming(List<LinkedHashMap<String,Object>> naming){
        log.info("\t >> Create Naming object to be send to Coleman in the Create Context in Coleman Service task <<  ");


        for (LinkedHashMap<String,Object> nomenclature : naming) {
            log.info("\t >> Found Naming with id : " + nomenclature.get("id") + " <<  ");
            // Create a JSON Object
            JSONObject namingObject = new JSONObject();
            namingObject.put("id", nomenclature.get("Id"));
            namingObject.put("label", nomenclature.get("Label"));
            namingObject.put("value", namingQuestionnaireService.getNamingModelValue(nomenclature.get("Id").toString()));
            // Fetch value from external service but I don't know which one yet

            // Send the JSON Object to Coleman Questionnaire
            webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                    .post()
                    .uri("/api/nomenclature")
                    .body(BodyInserters.fromValue(namingObject))
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            log.info("\t \t \t >> Naming sent to Coleman Questionnaire <<  ");
        }
    }

    public void createAndPostQuestionnaires(LinkedHashMap<String,Object> questionnaire){
        log.info("\t >> Create Questionnaires objects to be send to Coleman in the Create Context in Coleman Service task <<  ");


        List<String> listOfNamingIds = new ArrayList();
        LinkedHashMap<String,Object> requiredNomenclatures = (LinkedHashMap<String,Object>) (questionnaire.get("RequiredNomenclatures"));
        List<LinkedHashMap> listOfNaming = (List<LinkedHashMap>) requiredNomenclatures.get("Nomenclature");
        for (LinkedHashMap<String,Object> nomenclature : listOfNaming) {
            listOfNamingIds.add((String) nomenclature.get("Id"));
        }
        // Create a JSON Object
        JSONObject questionnaireObject = new JSONObject();
        questionnaireObject.put("id", questionnaire.get("Id"));
        questionnaireObject.put("label", questionnaire.get("Label"));
        questionnaireObject.put("requiredNomenclaturesIds", listOfNamingIds);
        questionnaireObject.put("value", namingQuestionnaireService.getQuestionnaireModelValue(questionnaire.get("Id").toString()));
        // Fetch value from external service but I don't know which one yet

        // Send the JSON Object to Coleman Questionnaire
        webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                .post()
                .uri("/api/questionnaire-models")
                .body(BodyInserters.fromValue(questionnaireObject))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        log.info("\t \t \t >> Naming sent to Coleman Questionnaire <<  ");
    }

    /**
     * Create Metadata object, from the incomplete object generated from process variables to be sent to Coleman Questionnaire
     * This object is a bit long, it might be interesting to create a dto for it
     *    - This might help to autogenerate empty fields
     */
    public void createAndPostMetadataObject(String id, String label, LinkedHashMap<String,Object> questionnaire, List<Map<String, Object>> variables, String inseeContext){
        log.info("\t >> Create Metadata object to be send to Coleman in the Create Context in Coleman Service task <<  ");


        //TODO: Re-do when there is more than one questionnaire
        List<String> listOfQuestionnaireIds = new ArrayList();

        listOfQuestionnaireIds.add((String) questionnaire.get("Id"));


        // Create a JSON Object
        JSONObject metadataObject = new JSONObject();
        metadataObject.put("id", id);
        metadataObject.put("label", label);
        metadataObject.put("metadata", new HashMap<>() {{
            put("variables", variables);
        }});
        metadataObject.put("questionnaireIds", listOfQuestionnaireIds);
        metadataObject.put("inseeContext", inseeContext);
        // Fetch value from external service but I don't know which one yet

        // Send the JSON Object to Coleman Questionnaire
        webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                .post()
                .uri("/api/metadata")
                .body(BodyInserters.fromValue(metadataObject))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

    }
}
