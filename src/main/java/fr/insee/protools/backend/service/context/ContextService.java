package fr.insee.protools.backend.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ContextService {
    /**
     * Process the uploaded file and store it's content in VARIABLE_NAME_SERIALIZED_CONTEXT_FILE engine variable
     * @param file
     * @param taskId
     */
    void processContextFileAndCompleteTask(MultipartFile file, String taskId);

    /**
     * Retrieve Protools Context from of a given processInstance ID
     *
     * @param processInstanceId
     * @return the Json context of the process associated with process Instance
     * @throws
     */
    JsonNode getContextByProcessInstance(String processInstanceId) ;
}
