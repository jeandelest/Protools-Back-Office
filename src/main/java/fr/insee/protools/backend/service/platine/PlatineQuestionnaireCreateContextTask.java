package fr.insee.protools.backend.service.platine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.exception.JsonParsingException;
import fr.insee.protools.backend.service.nomenclature.NomenclatureService;
import fr.insee.protools.backend.service.platine.questionnaire.PlatineQuestionnaireService;
import fr.insee.protools.backend.service.questionnaire_model.QuestionnaireModelService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Component
public class PlatineQuestionnaireCreateContextTask implements JavaDelegate {
    //TODO : Expose mandatory context?
    //TODO : éclater en 2 tâches pour éviter un retry sur les nomenclatures si ça plante dans models

    @Autowired ContextService protoolsContext;
    @Autowired ObjectMapper objectMapper;
    @Autowired NomenclatureService nomenclatureService;
    @Autowired QuestionnaireModelService questionnaireModelService;

    @Autowired PlatineQuestionnaireService platineQuestionnaireService;

    @Override
    public void execute(DelegateExecution execution) {
        JsonNode rootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());

        //Get the list of nomenclatures defined in Protools Context
        var nomenclatureIterator =rootNode.get(NOMENCLATURES).elements();
        while (nomenclatureIterator.hasNext()) {
            var node = nomenclatureIterator.next();
            String nomenclatureId  = node.get(ID).asText();
            String nomenclatureLabel= node.get(LABEL).asText();
            String nomenclatureValueStr = nomenclatureService.getNomenclatureContent(nomenclatureId);
            JsonNode nomenclatureValue = null;
            try {
                nomenclatureValue = objectMapper.readTree(nomenclatureValueStr);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException("Error while parsing the json retrieved for nomenclatureId="+nomenclatureId ,e);
            }

            //Write this nomenclature to platine
            platineQuestionnaireService.postNomenclature(nomenclatureId,nomenclatureLabel,nomenclatureValue);
            //TODO : handles the exceptions here?
        }

        //Get the list of nomenclatures defined in Protools Context
        var questionnaireModelIterator =rootNode.get(QUESTIONNAIRE_MODELS).elements();
        while (questionnaireModelIterator.hasNext()) {
            var node = questionnaireModelIterator.next();
            String questionnaireId  = node.get(ID).asText();
            String questionnaireLabel= node.get(LABEL).asText();
            String questionnaireValueStr= questionnaireModelService.getQuestionnaireModel(questionnaireId);

            JsonNode questionnaireValue = null;
            try {
                questionnaireValue = objectMapper.readTree(questionnaireValueStr);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException("Error while parsing the json retrieved for Model questionnaireId="+questionnaireId ,e);
            }


            JsonNode nomenclaturesArrayNode = node.get(REQUIRED_NOMENCLATURES);
            Set<String> requiredNomenclatures = new HashSet<>(nomenclaturesArrayNode.size());
            nomenclaturesArrayNode.forEach(jsonNode -> requiredNomenclatures.add(jsonNode.asText()));

            //Write this nomenclature to platine
            platineQuestionnaireService.postQuestionnaireModel(questionnaireId,questionnaireLabel,questionnaireValue,requiredNomenclatures);
            //TODO : handles the exceptions here?
        }
    }
}
