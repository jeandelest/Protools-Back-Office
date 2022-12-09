package com.protools.flowableDemo.services.coleman.createContext;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Retrieve the content of the context file and create the context in Coleman Pilotage & Questionnaire
 */
@Component
@Slf4j
public class CreateContextColemanServiceTask implements JavaDelegate {

    @Autowired
    private CreateColemanPilotageService createColemanPilotageService;

    @Autowired
    private CreateColemanQuestionnaireService createColemanQuestionnaireService;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Create Context into Coleman Pilotage & Questionnaire Service Task <<  ");

        // Coleman Questionnaire part
        //TODO : Message Quentin pour le contexte xml
        //For now I'll assume the context is correctly imported with the right name
        //I'll also assume that there is more than one naming
        List<LinkedHashMap<String,Object>> naming = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable("Nomenclature");
        createColemanQuestionnaireService.createAndPostNaming(naming);

        LinkedHashMap<String,Object> questionnaire = (LinkedHashMap<String,Object>) delegateExecution.getVariable("QuestionnaireModel");
        createColemanQuestionnaireService.createAndPostQuestionnaires(questionnaire);
        // Create Metadata object
        // TODO : Ask if we need to create a metadata dto object
        String id = (String) delegateExecution.getVariable("Id");
        String label = (String) delegateExecution.getVariable("Label");
        List<Map<String,Object>> variables = new ArrayList<>();
        String inseeContext= (String) delegateExecution.getVariable("InseeContext");

        variables.add(new HashMap<>() {{
            put("name", "Enq_Libelle_Enquete");
            put("value", delegateExecution.getVariable("LabelOperation"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_Objectif_Courts");
            put("value", delegateExecution.getVariable("Enq_Objectif_Courts"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_CaractereObligatoire");
            put("value", delegateExecution.getVariable("Enq_CaractereObligatoire"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_NumeroVisa");
            put("value", delegateExecution.getVariable("Enq_NumeroVisa"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_MinistereTutelle");
            put("value", delegateExecution.getVariable("Enq_MinistereTutelle"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_ParutionJo");
            put("value", delegateExecution.getVariable("Enq_ParutionJo"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_RespOperationnel");
            put("value", delegateExecution.getVariable("Enq_RespOperationnel"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_RespTraitement");
            put("value", delegateExecution.getVariable("Enq_RespTraitement"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_AnneeVisa");
            put("value", delegateExecution.getVariable("Enq_AnneeVisa"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_QualiteStatistique");
            put("value", delegateExecution.getVariable("Enq_QualiteStatistique"));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Loi_statistique");
            put("value", "");
        }});

        variables.add(new HashMap<>() {{
            put("name", "Loi_rgpd");
            put("value", "");
        }});

        variables.add(new HashMap<>() {{
            put("name", "Loi_informatique");
            put("value", "");
        }});

        createColemanQuestionnaireService.createAndPostMetadataObject(id,label,questionnaire,variables,inseeContext);

        //Coleman Pilotage Part

        List<Object> partitionsList = (List<Object>) delegateExecution.getVariable("Partition");
        LinkedHashMap<Object,Object> partitionsStr = (LinkedHashMap<Object, Object>) partitionsList.get(0);
        Gson gson = new Gson();
        Map<String, Object> partitions = gson.fromJson(gson.toJson(partitionsStr),Map.class);
        Map<String, Object> dateObject = (Map<String, Object>) partitions.get("Dates");
        String dateDebutCampagne = ((String) dateObject.get("CollectionStartDate"));
        String dateFinCampagne = ((String) dateObject.get("CollectionEndDate"));


        LocalDateTime startDate = LocalDateTime.parse(dateDebutCampagne,
                DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss") );

        long collectionStartDate = startDate
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        LocalDateTime endDate = LocalDateTime.parse(dateFinCampagne,
                DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss") );
        long collectionEndDate = endDate
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        createColemanPilotageService.createCampaign(collectionStartDate,collectionEndDate,id,label);
    }
}
