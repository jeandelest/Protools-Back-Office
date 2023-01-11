package com.protools.flowableDemo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.protools.flowableDemo.model.exceptions.FileNotFoundException;
import com.protools.flowableDemo.services.era.DrawDailySampleServiceTask;
import com.protools.flowableDemo.services.utils.UploadFileToEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Controller
public class FileUploadController {
    @Autowired
    UploadFileToEngineService uploadFileToEngineService;


    @CrossOrigin
    @Operation(summary = "Upload context file to the engine, the context file must be an xml file following a (not yet) defined format")
    @PostMapping("/upload-context")
    public ResponseEntity<Void> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("taskID") String taskID
    ) throws FileNotFoundException {
        uploadFileToEngineService.storeFile(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
