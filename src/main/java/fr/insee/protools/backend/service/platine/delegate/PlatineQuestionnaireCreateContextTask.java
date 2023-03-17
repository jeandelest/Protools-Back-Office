package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.exception.JsonParsingException;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.platine.questionnaire.PlatineQuestionnaireService;
import fr.insee.protools.backend.service.platine.questionnaire.dto.CampaignDto;
import fr.insee.protools.backend.service.platine.questionnaire.dto.MetadataDto;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Component
@Slf4j
public class PlatineQuestionnaireCreateContextTask implements JavaDelegate, DelegateContextVerifier {
    //TODO : Expose mandatory context?
    //TODO : éclater en 2 tâches pour éviter un retry sur les nomenclatures si ça plante dans models

    @Autowired ContextService protoolsContext;
    @Autowired ObjectMapper objectMapper;
    @Autowired NomenclatureService nomenclatureService;
    @Autowired QuestionnaireModelService questionnaireModelService;

    @Autowired PlatineQuestionnaireService platineQuestionnaireService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin",execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        //check context
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);

        //Get the list of nomenclatures defined in Protools Context
        var nomenclatureIterator =contextRootNode.get(NOMENCLATURES).elements();
        if(!nomenclatureIterator.hasNext()){
            log.info("ProcessInstanceId={} - does not declare any nomenclature",execution.getProcessInstanceId());
        }
        else  {
            Set<String> platineExistingNomenclatures = platineQuestionnaireService.getNomenclaturesId();
            while (nomenclatureIterator.hasNext()) {
                var node = nomenclatureIterator.next();
                String nomenclatureId  = node.get(ID).asText();
                String nomenclatureLabel= node.get(LABEL).asText();

                //check if platine know this nomenclature
                if(platineExistingNomenclatures.contains(nomenclatureId)){
                    log.info("ProcessInstanceId={} - nomenclature ID={} already exists in platine",execution.getProcessInstanceId(),nomenclatureId);
                }
                else {
                    String nomenclatureValueStr = nomenclatureService.getNomenclatureContent(nomenclatureId);
                    JsonNode nomenclatureValue = null;
                    try {
                        nomenclatureValue = objectMapper.readTree(nomenclatureValueStr);
                    } catch (JsonProcessingException e) {
                        throw new JsonParsingException("Error while parsing the json retrieved for nomenclatureId=" + nomenclatureId, e);
                    }

                    //Write this nomenclature to platine
                    platineQuestionnaireService.postNomenclature(nomenclatureId,nomenclatureLabel,nomenclatureValue);
                    //TODO : handles the exceptions here?
                    log.info("ProcessInstanceId={} - nomenclature ID={} created in platine",execution.getProcessInstanceId(),nomenclatureId);
                }

            }
        }


        //Get the list of Questionnaire Models defined in Protools Context
        var questionnaireModelIterator =contextRootNode.get(QUESTIONNAIRE_MODELS).elements();
        Set<String> questionnaireModelIds = new HashSet<>(); //Used to build the CampaignDto later
        while (questionnaireModelIterator.hasNext()) {
            var node = questionnaireModelIterator.next();
            String questionnaireId  = node.get(ID).asText();
            String questionnaireLabel= node.get(LABEL).asText();
            questionnaireModelIds.add(questionnaireId);

            //check if platine know this questionnaire
            if(platineQuestionnaireService.questionnaireModelExists(questionnaireId)){
                log.info("getProcessInstanceId={} - questionnaireId ID={} already exists in platine",execution.getProcessInstanceId(),questionnaireId);
            }
            else {

                String questionnaireValueStr = questionnaireModelService.getQuestionnaireModel(questionnaireId);

                JsonNode questionnaireValue = null;
                try {
                    questionnaireValue = objectMapper.readTree(questionnaireValueStr);
                } catch (JsonProcessingException e) {
                    throw new JsonParsingException("Error while parsing the json retrieved for Model questionnaireId=" + questionnaireId, e);
                }
                //get the list of nomenclatures needed by this Questionnaire Model
                JsonNode nomenclaturesArrayNode = node.get(REQUIRED_NOMENCLATURES);
                Set<String> requiredNomenclatures = new HashSet<>(nomenclaturesArrayNode.size());
                nomenclaturesArrayNode.forEach(jsonNode -> requiredNomenclatures.add(jsonNode.asText()));

                //TODO : should we check context coherence here and verify that we dont have any unknown nomenclature?
                //Write this questionnaire model  to platine
                platineQuestionnaireService.postQuestionnaireModel(questionnaireId, questionnaireLabel, questionnaireValue, requiredNomenclatures);
                //TODO : handles the exceptions here?
            }
        }
        CampaignDto campaignDto= CampaignDto.builder()
                .id(contextRootNode.path(ID).textValue())
                .label(contextRootNode.path(LABEL).textValue())
                .metadata(createMetadataDto(contextRootNode))
                .questionnaireIds(questionnaireModelIds)
                .build();
        platineQuestionnaireService.postCampaign(campaignDto);
        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());
    }




    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        Set<String> missingNodes = new HashSet<>();

        Set<String> requiredNodes =
                Set.of(ID,LABEL,NOMENCLATURES,QUESTIONNAIRE_MODELS,CTX_METADONNEE);
        Set<String> requiredMetadonnes =
                Set.of(LABEL_LONG_OPERATION,OBJECTIFS_COURTS,CARACTERE_OBLIGATOIRE,
                        NUMERO_VISA,MINISTERE_TUTELLE,PARUTION_JO,DATE_PARUTION_JO,
                        RESPONSABLE_OPERATIONNEL,RESPONSABLE_TRAITEMENT,ANNEE_VISA,
                        QUALITE_STATISTIQUE,TEST_NON_LABELLISE);

        missingNodes.addAll(computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        if (contextRootNode.get(CTX_METADONNEE) != null) {
            missingNodes.addAll(computeMissingChildrenMessages(requiredMetadonnes,contextRootNode.path(CTX_METADONNEE),getClass()));
        }
        return missingNodes;
    }


    private MetadataDto createMetadataDto(JsonNode contextRootNode){
        JsonNode metadataNode = contextRootNode.get(CTX_METADONNEE);
        return MetadataDto.builder()
                .Enq_LibelleEnquete(metadataNode.path(LABEL_LONG_OPERATION).asText())
                .Enq_ObjectifsCourts(metadataNode.path(OBJECTIFS_COURTS).asText())
                .Enq_CaractereObligatoire(metadataNode.path(CARACTERE_OBLIGATOIRE).asText())
                .Enq_NumeroVisa(metadataNode.path(NUMERO_VISA).asText())
                .Enq_MinistereTutelle(metadataNode.path(MINISTERE_TUTELLE).asText())
                .Enq_ParutionJo(metadataNode.path(PARUTION_JO).asText())
                .Enq_DateParutionJo(metadataNode.path(DATE_PARUTION_JO).asText())
                .Enq_RespOperationnel(metadataNode.path(RESPONSABLE_OPERATIONNEL).asText())
                .Enq_RespTraitement(metadataNode.path(RESPONSABLE_TRAITEMENT).asText())
                .Enq_AnneeVisa(metadataNode.path(ANNEE_VISA).asText())
                .Enq_QualiteStatistique(metadataNode.path(QUALITE_STATISTIQUE).asText())
                .Enq_TestNonLabellise(metadataNode.path(TEST_NON_LABELLISE).asText())
                .Loi_statistique("")
                .Loi_rgpd("")
                .Loi_informatique("")
                .build();
    }

}
