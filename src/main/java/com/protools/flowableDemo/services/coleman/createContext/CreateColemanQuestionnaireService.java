package com.protools.flowableDemo.services.coleman.createContext;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;


import java.util.*;

import static com.protools.flowableDemo.services.utils.ContextConstants.*;

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
            log.info("\t >> Found Naming with id : " + nomenclature.get(ID) + " <<  ");
            // Create a JSON Object
            var namingObject = new HashMap<String, Object>();
            namingObject.put("id", nomenclature.get(ID));
            namingObject.put("label", nomenclature.get(LABEL));
            //namingObject.put("value","[ {\"id\": \"01\", \"label\": \"AIN (01)\"}]");
            namingObject.put("value", namingQuestionnaireService.getNamingModelValue(nomenclature.get("id").toString()));
            // Fetch value from external service but I don't know which one yet
            var objectMapper = new ObjectMapper();
            String requestBody =null;
            try {
                requestBody = objectMapper
                        .writeValueAsString(namingObject);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            // Send the JSON Object to Coleman Questionnaire
            webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/api/nomenclature").build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
            log.info("\t \t \t >> Naming sent to Coleman Questionnaire <<  ");
        }
    }

    public void createAndPostQuestionnaires(LinkedHashMap<String,Object> questionnaire){
        log.info("\t >> Create Questionnaires objects to be send to Coleman in the Create Context in Coleman Service task <<  ");


        List<String> listOfNamingIds = new ArrayList();
        LinkedHashMap<String,Object> requiredNomenclatures = (LinkedHashMap<String,Object>) (questionnaire.get(REQUIRED_NOMENCLATURES));
        List<LinkedHashMap> listOfNaming = (List<LinkedHashMap>) requiredNomenclatures.get(NOMENCLATURE);
        for (LinkedHashMap<String,Object> nomenclature : listOfNaming) {
            listOfNamingIds.add((String) nomenclature.get("Id"));
        }
        // Create a JSON Object
        var questionnaireObject = new HashMap<String, Object>();
        questionnaireObject.put("id", questionnaire.get(ID));
        questionnaireObject.put("label", questionnaire.get(LABEL));
        questionnaireObject.put("requiredNomenclaturesIds", listOfNamingIds);
        /* questionnaireObject.put("value", "{\"id\":\"lab5elzw\"," +
                "\"modele\":\"ENQFAMI22\"," +
                "\"enoCoreVersion\":\"2.3.10-controls-type\"}"); */
        questionnaireObject.put("value", namingQuestionnaireService.getQuestionnaireModelValue(questionnaire.get("Id").toString()));
        // Fetch value from external service but I don't know which one yet

        var objectMapper = new ObjectMapper();
        String requestBody =null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(questionnaireObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // Send the JSON Object to Coleman Questionnaire
        webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/questionnaire-models").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
        log.info("\t \t \t >> Questionnaire sent to Coleman Questionnaire <<  ");
    }

    /**
     * Create Metadata object, from the incomplete object generated from process variables to be sent to Coleman Questionnaire
     * This object is a bit long, it might be interesting to create a dto for it
     *    - This might help to autogenerate empty fields
     */
    public void createAndPostMetadataObject(String id, String label,List<LinkedHashMap<String,Object>> questionnaire, List<Map<String, Object>> variables, String inseeContext){
        log.info("\t >> Create Metadata object to be send to Coleman in the Create Context in Coleman Service task <<  ");


        //TODO: Re-do when there is more than one questionnaire
        List<String> listOfQuestionnaireIds = new ArrayList();
        for (LinkedHashMap<String,Object> questionnaireModel: questionnaire){
            listOfQuestionnaireIds.add((String) questionnaireModel.get(ID));

        }


        // Create a JSON Object
        var metadataObject = new HashMap<String, Object>();
        metadataObject.put("id", id);
        metadataObject.put("label", label);
        metadataObject.put("metadata",  new HashMap<>() {{
            put("value",new HashMap<>() {{
                put("variables", variables);
                put("inseeContext", inseeContext);
            }}
            );


        }});
        metadataObject.put("questionnaireIds", listOfQuestionnaireIds);

        // Fetch value from external service but I don't know which one yet
        var objectMapper = new ObjectMapper();
        String requestBody =null;
        try {
            requestBody = objectMapper
                    .writeValueAsString(metadataObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // Send the JSON Object to Coleman Questionnaire
        webClientHelper.getWebClientForRealm(realm,colemanQuestionnaireUri)
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/campaigns").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();
    }
}
