package fr.insee.protools.backend.controller;

import fr.insee.protools.backend.service.context.ContextServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RequestMapping("/protools-process")
@Controller
public class ProtoolsProcessController {
    @Autowired ContextServiceImpl contextService;

    @PostMapping(value = "/upload-context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload of the context file",
            description = "Upload the context of the process containing all the metadata used during the process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "The received file is not a valid XML"),
            @ApiResponse(responseCode = "404", description = "The taskId does not exist"),
            @ApiResponse(responseCode = "415", description = "The received file is not an XML file"),
            @ApiResponse(responseCode = "422", description = "The received file is a valid XML bad has incorrect content")
    })
    public ResponseEntity<Void> uploadFile(
            @Parameter(name = "file", description = "XML file with the context", required = true)
            @RequestPart (name = "file") MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        contextService.processContextFileAndCompleteTask(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
