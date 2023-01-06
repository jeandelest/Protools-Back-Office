package com.protools.flowableDemo.services.coleman.createContext;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.protools.flowableDemo.services.utils.ContextConstants.*;

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


        String id = (String) delegateExecution.getVariable(ID);
        log.info("ID suvey: "+id);
        String label = (String) delegateExecution.getVariable(LABEL);
        List<Object> partitionsList = (List<Object>) delegateExecution.getVariable(PARTITION);
        LinkedHashMap<Object,Object> partitionsStr = (LinkedHashMap<Object, Object>) partitionsList.get(0);
        Gson gson = new Gson();
        Map<String, Object> partitions = gson.fromJson(gson.toJson(partitionsStr),Map.class);
        Map<String, Object> dateObject = (Map<String, Object>) partitions.get(DATES);
        String dateDebutCampagne = ((String) dateObject.get(DATE_DEBUT_COLLECTE));
        String dateFinCampagne = ((String) dateObject.get(DATE_FIN_COLLECTE));


        // Coleman Questionnaire part
        // Create Metadata object
        // TODO : Ask if we need to create a metadata dto object
        List<Map<String,Object>> variables = new ArrayList<>();
        String inseeContext= (String) delegateExecution.getVariable(CONTEXTE);

        variables.add(new HashMap<>() {{
            put("name", "Enq_Libelle_Enquete");
            put("value", delegateExecution.getVariable(LABEL_COURT_OPERATION));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_Objectif_Courts");
            put("value", delegateExecution.getVariable(OBJECTIFS_COURTS));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_CaractereObligatoire");
            put("value", delegateExecution.getVariable(CARACTERE_OBLIGATOIRE));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_NumeroVisa");
            put("value", delegateExecution.getVariable(NUMERO_VISA));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_MinistereTutelle");
            put("value", delegateExecution.getVariable(MINISTERE_TUTELLE));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_ParutionJo");
            put("value", delegateExecution.getVariable(PARUTION_JO));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_RespOperationnel");
            put("value", delegateExecution.getVariable(RESPONSABLE_OPERATIONNEL));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_RespTraitement");
            put("value", delegateExecution.getVariable(RESPONSABLE_TRAITEMENT));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_AnneeVisa");
            put("value", delegateExecution.getVariable(ANNEE_VISA));
        }});

        variables.add(new HashMap<>() {{
            put("name", "Enq_QualiteStatistique");
            put("value", delegateExecution.getVariable(QUALITE_STATISTIQUE));
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


        //For now I'll assume the context is correctly imported with the right name
        //I'll also assume that there is more than one naming
        List<LinkedHashMap<String,Object>> naming = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable(NOMENCLATURE);
        createColemanQuestionnaireService.createAndPostNaming(naming);

        ArrayList<LinkedHashMap<String,Object>> questionnaire = (ArrayList) delegateExecution.getVariable(QUESTIONNAIRE_MODEL);
        createColemanQuestionnaireService.createAndPostMetadataObject(id,label,questionnaire,variables,inseeContext);
        for (LinkedHashMap<String,Object> questionnaireModel: questionnaire){
            createColemanQuestionnaireService.createAndPostQuestionnaires(questionnaireModel);
        }


        //Coleman Pilotage Part

        long collectionStartDate = Instant.parse(dateDebutCampagne).toEpochMilli();

        long collectionEndDate = Instant.parse(dateFinCampagne).toEpochMilli();

        createColemanPilotageService.createCampaign(collectionStartDate,collectionEndDate,id,label);





    }
}
