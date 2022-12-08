package com.protools.flowableDemo.services.coleman.createContext;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieve the content of the context file and create the context in Coleman Pilotage & Questionnaire
 */
@Component
@Slf4j
public class CreateContextColemanServiceTask implements JavaDelegate {
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Create Context into Coleman Pilotage & Questionnaire Service Task <<  ");

        // Coleman Questionnaire part
        //TODO : Message Quentin pour le contexte xml
        //For now I'll assume the context is correctly imported with the right name
        //I'll also assume that there is more than one naming
        List<LinkedHashMap<String,Object>> naming = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable("Nomenclature");

        LinkedHashMap<String,Object> questionnaire = (LinkedHashMap<String,Object>) delegateExecution.getVariable("QuestionnaireModel");

        // Create Metadata object
        // TODO : Ask if we need to create a metadata dto object
        Map<String,Object> metadata = new LinkedHashMap<>();
        List<Map<String,Object>> variables = new ArrayList<>();







        //Coleman Pilotage Part
        String id = (String) delegateExecution.getVariable("Id");
        String label = (String) delegateExecution.getVariable("Label");

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
    }
}
