package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.Utils.FileStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload-context")
    public void uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        fileStorageService.storeFile(file, taskID);

    }
}
