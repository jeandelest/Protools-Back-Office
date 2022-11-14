package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.utils.RessourceUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.TransformerException;
import java.io.IOException;

@RestController
@Slf4j
public class BpmnExportController {

    @Autowired
    private RessourceUtils ressourceUtils;

    @CrossOrigin
    @Operation(summary = "Return BPMN file (String format) by processKey")
    @GetMapping(value = "/getBPMNFile/{processKey}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getBPMNInfo(@PathVariable String processKey) throws IOException, TransformerException {
        log.info("\t >> Getting BPMN file (String format) with processKey : "+processKey);
        return ResponseEntity.status(HttpStatus.OK).body((RessourceUtils.getResourceFileAsString(processKey)));
    }
}
