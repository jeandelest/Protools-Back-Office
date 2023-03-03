package fr.insee.protools.backend.service.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import fr.insee.protools.backend.exception.TaskNotFoundException;
import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.context.exception.BadContextNotXMLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.TaskService;
import org.flowable.eventregistry.json.converter.FlowableEventJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Service
@Slf4j
public class ContextService {

    @Autowired
    private TaskService taskService;

    private static final Gson gson = new Gson();

    /**
     * Process the uploaded file and store it's content in VARIABLE_NAME_SERIALIZED_CONTEXT_FILE engine variable
     * @param file
     * @param taskId
     */
    public void processContextFileAndCompleteTask(MultipartFile file, String taskId) {
        //Validate file name (XML)
        var fileExtension = getFileExtension(file.getOriginalFilename());
        if (fileExtension.isEmpty()) {
            throw new BadContextNotXMLException(String.format("Uploaded file %s has incorrect filename without extension", file.getOriginalFilename()));
        } else if (!fileExtension.get().equalsIgnoreCase("xml")) {
            throw new BadContextNotXMLException(String.format("Uploaded file %s has incorrect extension. Expected xml", file.getOriginalFilename()));
        }

        try {
            // Serialize the file content
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.debug("Context File content : " + content);

            Map<String, Object> variables = parseXmlToVariableMap(content);

            // Extraction of campaign TIMER START/END dates
            // TODO : warning : currently we are only handling a single partition
            Object firstPartition = variables.getOrDefault(PARTITION, Collections.emptyList());
            if(!(firstPartition instanceof List partitions )||partitions.isEmpty()){
                throw new BadContextIncorrectException(String.format("Missing %s in context",PARTITION));
            }
            Pair<LocalDateTime, LocalDateTime> startEndDT = getCollectionStartAndEndFromPartition((Map<Object, Object>) partitions.get(0));
            //add these variables to the list
            variables.put(DATE_DEBUT_COLLECTE,startEndDT.getKey());
            variables.put(DATE_FIN_COLLECTE,startEndDT.getValue());

            // Complete task & store the file content into the engine
            taskService.complete(taskId, variables);
        } catch (IOException e) {
            throw new BadContextIOException("Error while reading context content", e);
        }
        catch (FlowableObjectNotFoundException e){
            throw new TaskNotFoundException(taskId);
        }
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }


    private static Map<String, Object>  parseXmlToVariableMap(String xmlFile) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            JsonNode node = xmlMapper.readTree(xmlFile);
            log.info("Xml file parsed into JsonNode: " + node);
            return convertJsonToVariableMap(node);
        }
        catch (JsonProcessingException e) {
            String message = "Failed to transfort XML file into process context JSON";
            log.info(message, e);
            throw new FlowableEventJsonException(message, e);
        }
    }

    private static Map<String, Object> convertJsonToVariableMap(JsonNode node) {
        //Récupération premier niveau et init de la clé
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(node, new TypeReference<>() {
        });
        Map<String, Object> newVariables = new HashMap<>();
        //Iterate over result map
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (entry.getValue() instanceof LinkedHashMap) {
                Map<String,Object> subMap = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String,Object> subEntry : subMap.entrySet()){
                    log.trace("{} : {} of type ",subEntry.getKey() , subEntry.getValue() ,  subEntry.getValue().getClass());
                    if (newVariables.containsKey(subEntry.getKey())){
                        //TODO: to be done cleanly
                        // C'est super sale, mais ça suffit pour le moment
                        newVariables.put(subEntry.getKey()+".1", newVariables.remove(subEntry.getKey()));
                        newVariables.put(subEntry.getKey()+".2",subEntry.getValue());
                    } else {
                        newVariables.put(subEntry.getKey(),subEntry.getValue());}
                }
            } else {
                newVariables.put(entry.getKey(),entry.getValue());
            }
        }

        result.putAll(newVariables);
        return newVariables;
    }
    private static Pair<LocalDateTime,LocalDateTime> getCollectionStartAndEndFromPartition(Map<Object, Object> partitionsStr){
        Map<String, Object> partitions = gson.fromJson(gson.toJson(partitionsStr), Map.class);
        Map<String, Object> dateObject = (Map<String, Object>) partitions.get(DATES);
        try {
            LocalDateTime collectionStart = LocalDateTime.parse(((String) dateObject.get(DATE_DEBUT_COLLECTE)), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime collectionEnd = LocalDateTime.parse(((String) dateObject.get(DATE_FIN_COLLECTE)), DateTimeFormatter.ISO_DATE_TIME);
            log.info("CollectionStartDate={} - CollectionEndDate={}", collectionStart, collectionEnd);
            return Pair.of(collectionStart, collectionEnd);
        }
        catch (DateTimeParseException e){
            throw new BadContextIncorrectException(String.format("%s or %s cannot be casted to DateTime : %s",DATE_DEBUT_COLLECTE,DATE_FIN_COLLECTE,e.getMessage()));
        }
    }
}
