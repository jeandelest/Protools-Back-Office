package fr.insee.protools.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.controller.exeptions.BadVariablesParamBPMNError;
import fr.insee.protools.backend.controller.requests.ProtoolsProcessInstanceWithContextCreateRequest;
import fr.insee.protools.backend.service.context.ContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.rest.service.api.RestResponseFactory;
import org.flowable.rest.service.api.engine.variable.RestVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/protools-process")
@Controller
@Slf4j
public class ProtoolsProcessController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ContextService contextService;

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @PostMapping(value = "/upload-context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload of the context file",
            description = "Upload the context of the process containing all the metadata used during the process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "The received file is not a valid JSON"),
            @ApiResponse(responseCode = "404", description = "The taskId does not exist"),
            @ApiResponse(responseCode = "415", description = "The received file is not an JSON file"),
            @ApiResponse(responseCode = "422", description = "The received file is a valid JSON bad has incorrect content")
    })
    public ResponseEntity<Void> uploadFile(
            @Parameter(name = "file", description = "JSON file with the context", required = true)
            @RequestPart (name = "file") MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        contextService.processContextFileAndCompleteTask(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }


        @PostMapping(value = "/create_process_instance_with_context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Start a process instance providing and provide de protools context",
            description = "Start a process instance with provided *processDefinitionId* and *businessKey* "
            + "and the protools JSON context similary with what is done by /runtime/process-instances" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "The received file is not a valid JSON"),
            @ApiResponse(responseCode = "404", description = "The processDefinitionId does not exist"),
            @ApiResponse(responseCode = "415", description = "The received file is not an JSON file"),
            @ApiResponse(responseCode = "422", description = "The received file is a valid JSON bad has incorrect content")
    })
    public ResponseEntity<Void> createProcessInstanceWithContext(
            @Parameter(name = "file", description = "JSON file with the context", required = true)
            @RequestPart (name = "file") MultipartFile file,

            @Parameter(name = "variables", description = "JSON file with customization of the process variables", required = false)
            @RequestPart (name = "configuration", required = false) Optional<MultipartFile> variables,

            @RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam(name="businessKey") String businessKey
            )
    {
        Optional<Map<String, Object>> startVariables = variables.flatMap(multipartFile -> {
            try {
                var request = objectMapper.readValue(multipartFile.getInputStream(), ProtoolsProcessInstanceWithContextCreateRequest.class);
                Map<String, Object> varMap = new HashMap<>();
                for (RestVariable variable : request.getVariables()) {
                    if (variable.getName() == null) {
                        throw new BadVariablesParamBPMNError("The passed json file for customization is incorrect (no name found)");
                    }
                    try {
                        varMap.put(variable.getName(), restResponseFactory.getVariableValue(variable));
                    }
                    catch (FlowableIllegalArgumentException e){
                        throw new BadVariablesParamBPMNError("The passed json file for customization is incorrect : "+e.getMessage());
                    }
                }
                return Optional.of(varMap);
            } catch (IOException e) {
               log.error("The passed json file cannot be converted to an array of variables");
               throw new BadVariablesParamBPMNError("The passed json file for customization cannot be read");
            }
        });

        contextService.processContextFileAndCreateProcessInstance(file, processDefinitionId, businessKey, startVariables);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
