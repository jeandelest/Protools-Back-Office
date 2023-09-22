package fr.insee.protools.backend.service.common.platine_sabiane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign.CampaignDto;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.campaign.MetadataValue;
import fr.insee.protools.backend.service.exception.JsonParsingBPMNError;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Slf4j
public class QuestionnaireHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void createQuestionnaire(JsonNode contextRootNode,
                                           QuestionnairePlatineSabianeService questionnairePlatineSabianeService,
                                           NomenclatureService nomenclatureService,
                                           QuestionnaireModelService questionnaireModelService,
                                           String processInstanceId,
                                           MetadataValue metadataDto
                                           ){
        //Get the list of nomenclatures defined in Protools Context
        //Create them if needed
        var nomenclatureIterator =contextRootNode.path(CTX_NOMENCLATURES).elements();
        if(!nomenclatureIterator.hasNext()){
            log.info("ProcessInstanceId={} - does not declare any nomenclature",processInstanceId);
        }
        else  {
            initRequiredNomenclatures(questionnairePlatineSabianeService, nomenclatureService,processInstanceId, nomenclatureIterator);
        }

        //Get the list of Questionnaire Models defined in Protools Context
        Set<String> questionnaireModelIds = initQuestionnaireModels(questionnairePlatineSabianeService,questionnaireModelService,processInstanceId, contextRootNode);
        CampaignDto campaignDto= CampaignDto.builder()
                .id(contextRootNode.path(CTX_CAMPAGNE_ID).textValue())
                .label(contextRootNode.path(CTX_CAMPAGNE_LABEL).textValue())
                .metadata(metadataDto)
                .questionnaireIds(questionnaireModelIds)
                .build();
        questionnairePlatineSabianeService.postCampaign(campaignDto);
    }

    static void initRequiredNomenclatures(QuestionnairePlatineSabianeService questionnairePlatineSabianeService, NomenclatureService nomenclatureService, String processInstanceId, Iterator<JsonNode> nomenclatureIterator) {
        //Get the list of existing nomenclatures on platine
        Set<String> existingNomenclatures = questionnairePlatineSabianeService.getNomenclaturesId();

        //Create the nomenclatures not existing yet on platine
        while (nomenclatureIterator.hasNext()) {
            var node = nomenclatureIterator.next();
            String nomenclatureId  = node.get(CTX_NOMENCLATURE_ID).asText();
            String nomenclatureCheminRepertoire  = node.path(CTX_NOMENCLATURE_CHEMIN_REPERTOIRE).asText();
            String nomenclatureLabel= node.get(CTX_NOMENCLATURE_LABEL).asText();
            //check if platine know this nomenclature
            if(existingNomenclatures.contains(nomenclatureId)){
                log.info("ProcessInstanceId={} - nomenclature ID={} already exists in collect platform ", processInstanceId,nomenclatureId);
            }
            else {
                //Retrieve the nomenclature from remote source
                String nomenclatureValueStr = nomenclatureService.getNomenclatureContent(nomenclatureId,nomenclatureCheminRepertoire);
                JsonNode nomenclatureValue;
                try {
                    nomenclatureValue = objectMapper.readTree(nomenclatureValueStr);
                } catch (JsonProcessingException e) {
                    throw new JsonParsingBPMNError("Error while parsing the json retrieved for nomenclatureId=" + nomenclatureId, e);
                }

                //Write this nomenclature to platine/sabiane
                questionnairePlatineSabianeService.postNomenclature(nomenclatureId,nomenclatureLabel,nomenclatureValue);
                //TODO : handles the exceptions here?
                log.info("ProcessInstanceId={} - nomenclature ID={} created in remote collect platform", processInstanceId,nomenclatureId);
            }
        }
    }

    /**
     * For every questionnaireModel defined in context : check if it exists in platine/sabiane questionnaire.
     * If it does not exists : retrieve it from questionnaireModelService and create it in platine/sabiane
     *
     * @param questionnairePlatineSabianeService : the service object used to access Sabiane Or Platine
     * @param questionnaireModelService : The service object used to retrieve questionnaire models (ex: from gitlab)
     * @param processInstanceId : used in logs
     * @param contextRootNode : the protools context
     * @return the set of questionnaireModel ids
     */
    public static Set<String> initQuestionnaireModels(QuestionnairePlatineSabianeService questionnairePlatineSabianeService, QuestionnaireModelService questionnaireModelService, String processInstanceId, JsonNode contextRootNode) {
        var questionnaireModelIterator = contextRootNode.path(CTX_QUESTIONNAIRE_MODELS).elements();
        Set<String> questionnaireModelIds = new HashSet<>(); //Used to build the CampaignDto later
        while (questionnaireModelIterator.hasNext()) {
            var node = questionnaireModelIterator.next();
            String questionnaireId = node.get(CTX_QUESTIONNAIRE_MODEL_ID).asText();
            String questionnaireCheminRepertoire = node.get(CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE).asText();
            String questionnaireLabel = node.get(CTX_QUESTIONNAIRE_MODEL_LABEL).asText();
            questionnaireModelIds.add(questionnaireId);

            //check if platine/sabiane know this questionnaire
            if (questionnairePlatineSabianeService.questionnaireModelExists(questionnaireId)) {
                log.info("getProcessInstanceId={} - questionnaireId ID={} already exists", processInstanceId, questionnaireId);
            } else {
                //Get the questionnaire model from remote source
                String questionnaireValueStr = questionnaireModelService.getQuestionnaireModel(questionnaireId, questionnaireCheminRepertoire);

                JsonNode questionnaireValue;
                try {
                    questionnaireValue = objectMapper.readTree(questionnaireValueStr);
                } catch (JsonProcessingException e) {
                    throw new JsonParsingBPMNError("Error while parsing the json retrieved for Model questionnaireId=" + questionnaireId, e);
                }
                //get the list of nomenclatures needed by this Questionnaire Model
                JsonNode nomenclaturesArrayNode = node.get(CTX_QUESTIONNAIRE_MODEL_REQUIRED_NOMENCLATURES);
                Set<String> requiredNomenclatures = new HashSet<>(nomenclaturesArrayNode.size());
                nomenclaturesArrayNode.forEach(jsonNode -> requiredNomenclatures.add(jsonNode.asText()));

                //TODO : should we check context coherence here and verify that we dont have any unknown nomenclature?
                //Write this questionnaire model  to platine/sabiane
                questionnairePlatineSabianeService.postQuestionnaireModel(questionnaireId, questionnaireLabel, questionnaireValue, requiredNomenclatures);
                //TODO : handles the exceptions here?
            }
        }
        return questionnaireModelIds;
    }

    public static Set<String> getContextErrors(JsonNode contextRootNode){
        if(contextRootNode==null){
            return Set.of(String.format("Class=%s : Context is missing ", QuestionnaireHelper.class.getSimpleName()));
        }

        Set<String> requiredNodes =
                Set.of(CTX_CAMPAGNE_ID, CTX_CAMPAGNE_LABEL, CTX_NOMENCLATURES, CTX_QUESTIONNAIRE_MODELS, CTX_METADONNEES);
        Set<String> missingNodes = new HashSet<>(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes, contextRootNode, QuestionnaireHelper.class));

        if (contextRootNode.get(CTX_NOMENCLATURES) != null) {
            //Check on nomenclatures
            var nomenclatureIterator =contextRootNode.path(CTX_NOMENCLATURES).elements();
            int i=0;
            while (nomenclatureIterator.hasNext()) {
                i++;
                var nomenclatureNode = nomenclatureIterator.next();
                if(nomenclatureNode.get(CTX_NOMENCLATURE_ID) == null || nomenclatureNode.get(CTX_NOMENCLATURE_ID).asText().isEmpty()){
                    missingNodes.add(missingCTXMessage(CTX_NOMENCLATURES,i,CTX_NOMENCLATURE_ID));
                }
                if(nomenclatureNode.get(CTX_NOMENCLATURE_LABEL) == null || nomenclatureNode.get(CTX_NOMENCLATURE_LABEL).asText().isEmpty()){
                    missingNodes.add(missingCTXMessage(CTX_NOMENCLATURES,i,CTX_NOMENCLATURE_LABEL));
                }
                if(nomenclatureNode.get(CTX_NOMENCLATURE_CHEMIN_REPERTOIRE) == null || nomenclatureNode.get(CTX_NOMENCLATURE_CHEMIN_REPERTOIRE).asText().isEmpty()){
                    missingNodes.add(missingCTXMessage(CTX_NOMENCLATURES,i,CTX_NOMENCLATURE_CHEMIN_REPERTOIRE));
                }
            }

        }

        if (contextRootNode.get(CTX_QUESTIONNAIRE_MODELS) != null) {
            //Check on questionnaire models
            var questionnaireModelsIterator = contextRootNode.get(CTX_QUESTIONNAIRE_MODELS).elements();
            int i = 0;
            while (questionnaireModelsIterator.hasNext()) {
                i++;
                var nomenclatureNode = questionnaireModelsIterator.next();
                if (nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_ID) == null || nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_ID).asText().isEmpty()) {
                    missingNodes.add(missingCTXMessage( CTX_QUESTIONNAIRE_MODELS, i, CTX_QUESTIONNAIRE_MODEL_ID));
                }
                if (nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_LABEL) == null || nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_LABEL).asText().isEmpty()) {
                    missingNodes.add(missingCTXMessage( CTX_QUESTIONNAIRE_MODELS, i, CTX_QUESTIONNAIRE_MODEL_LABEL));
                }
                if (nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE) == null || nomenclatureNode.get(CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE).asText().isEmpty()) {
                    missingNodes.add(missingCTXMessage( CTX_QUESTIONNAIRE_MODELS, i, CTX_QUESTIONNAIRE_MODEL_CHEMIN_REPERTOIRE));
                }
            }
        }
        return missingNodes;
    }

    private static String missingCTXMessage(String parent,int index,String child){
        return DelegateContextVerifier.computeMissingMessage(String.format("%s[%s].%s", parent, index, child), QuestionnaireHelper.class);
    }

    private QuestionnaireHelper(){}
}
