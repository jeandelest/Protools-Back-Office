package fr.insee.protools.backend.service.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.context.exception.BadContextNotJSONException;
import fr.insee.protools.backend.service.exception.TaskNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CONTEXT;
import static fr.insee.protools.backend.service.context.ContextConstants.*;

@Service
@Slf4j
public class ContextServiceImpl implements ContextService{

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectReader defaultReader = mapper.reader(); // maybe with configs

    private static  Map<String, JsonNode> contextCache = new ConcurrentHashMap<>();
    @Override
    public void processContextFileAndCompleteTask(MultipartFile file, String taskId) {
        //Check if task exists
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task==null){
            throw new TaskNotFoundException(taskId);
        }

        //Validate file name (JSON)
        var fileExtension = getFileExtension(file.getOriginalFilename());
        if (fileExtension.isEmpty()) {
            throw new BadContextNotJSONException(String.format("Uploaded file %s has incorrect filename without extension", file.getOriginalFilename()));
        } else if (!fileExtension.get().equalsIgnoreCase("json")) {
            throw new BadContextNotJSONException(String.format("Uploaded file %s has incorrect extension. Expected json", file.getOriginalFilename()));
        }


        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.debug("Context File content : " + content);

            JsonNode rootContext = defaultReader.readTree(content);

            //Variables to store for this process
            Map<String, Object> variables = new HashMap<>();
            //Store the raw json as string
            variables.put(VARNAME_CONTEXT,content);

            // Extraction of campaign TIMER START/END dates
            //        Do extraction of important BPMN Variables in separates functions
            JsonNode partitions = rootContext.path(CTX_PARTITIONS);
            if(partitions.isMissingNode() ){
                throw new BadContextIncorrectException(String.format("Missing %s in context", CTX_PARTITIONS));
            }
            var partitionsIterator = partitions.iterator();
            while (partitionsIterator.hasNext()){
                JsonNode partition = partitionsIterator.next();
                Pair<LocalDateTime, LocalDateTime> startEndDT = getCollectionStartAndEndFromPartition(partition);
                //add these variables to the list
                //TODO : distinguer les noms dans le json des noms de variables?
                String var_keyStart = String.format("partition_%s_%s",partition.path(CTX_PARTITION_ID), CTX_PARTITION_DATE_DEBUT_COLLECTE);
                String var_keyEnd = String.format("partition_%s_%s",partition.path(CTX_PARTITION_ID), CTX_PARTITION_DATE_FIN_COLLECTE);
                variables.put(var_keyStart,startEndDT.getKey());
                variables.put(var_keyEnd,startEndDT.getValue());
            }

            //Store in cache
            String processInstanceId = task.getProcessInstanceId();
            contextCache.put(processInstanceId,rootContext);

            // Complete task & store the file content into the engine
            taskService.complete(taskId, variables);
        } catch (IOException e) {
            throw new BadContextIOException("Error while reading context content", e);
        }
        catch (FlowableObjectNotFoundException e){
            throw new TaskNotFoundException(taskId);
        }
    }

    @Override
    public JsonNode getContextByProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new FlowableObjectNotFoundException("Could not find a process instance with id '" + processInstanceId + "'.", ProcessInstance.class);
        }

        JsonNode result = contextCache.get(processInstanceId);

        //If value does not exist in cache yet : Retrieve it and update cache
        if(result==null){
            String contextStr = runtimeService.getVariable(processInstanceId, VARNAME_CONTEXT,String.class);
            try {
                result=defaultReader.readTree(contextStr);
            } catch (JsonProcessingException e) {
                throw new BadContextIncorrectException(String.format("Context retrieved from engine could not be parsed for processInstanceId=[%s]",processInstanceId),e);
            }
            contextCache.put(processInstanceId,result);
        }
        return result;
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    //TODO : soit les json schema permettent de valider les dates, soit il faudra valider toutes les dates comme ça
   public static Pair<LocalDateTime,LocalDateTime> getCollectionStartAndEndFromPartition(JsonNode partitionNode){
       //TODO : handle multiple partition
       String start =partitionNode.get(CTX_PARTITION_DATE_DEBUT_COLLECTE).asText();
       String end   =partitionNode.get(CTX_PARTITION_DATE_DEBUT_COLLECTE).asText();

        try {
            LocalDateTime collectionStart = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime collectionEnd = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
            log.info("CollectionStartDate={} - CollectionEndDate={}", collectionStart, collectionEnd);
            return Pair.of(collectionStart, collectionEnd);
        }
        catch (DateTimeParseException e){
            throw new BadContextIncorrectException(String.format("%s or %s cannot be casted to DateTime : %s", CTX_PARTITION_DATE_DEBUT_COLLECTE, CTX_PARTITION_DATE_FIN_COLLECTE,e.getMessage()));
        }
    }
}
