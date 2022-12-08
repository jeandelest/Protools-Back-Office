package com.protools.flowableDemo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping("/fileuploadcontroller")
public class FileUploadController {
    @Autowired
    UploadFileToEngineService uploadFileToEngineService;


    //TODO : TO BE REMOVE
    @Autowired DrawDailySampleServiceTask era;
    public FileUploadController(UploadFileToEngineService uploadFileToEngineService) {
        this.uploadFileToEngineService = uploadFileToEngineService;
    }

    @CrossOrigin
    @Operation(summary = "Upload context file to the engine, the context file must be an xml file following a (not yet) defined format")
    @PostMapping("/upload-context")
    public ResponseEntity<Void> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        uploadFileToEngineService.storeFile(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    //TODO : to be removed
    @GetMapping("/toto")
    public ResponseEntity<List<Map>> toto(
          ) throws ParseException, JsonProcessingException {
        var res = era.getSampleIDs();
        return ResponseEntity.ok(res);
    }
}
