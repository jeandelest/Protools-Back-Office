package com.protools.flowableDemo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.util.Objects;

@RestController
@Slf4j
public class BpmnExportController {
    @CrossOrigin
    @Operation(summary = "Return BPMN file (String format) by processKey")
    @GetMapping(value = "/getBPMNFile/{processKey}", produces = MediaType.APPLICATION_XML_VALUE)
    public Source getBPMNInfo(@PathVariable String processKey) {
        log.info("\t >> Getting BPMN file (String format) with processKey : "+processKey);
        var inputStream= Objects.requireNonNull(
                BpmnExportController.class.getResourceAsStream(
                        "/processes/"+preventMaliciousInjections(processKey)+".bpmn20.xml")
        );
        return new StreamSource(inputStream);
    }

    private String preventMaliciousInjections(String processKey) {
        //TODO Implement controls to prevent malicious injections
        log.warn("Implement controls in this method to prevent malicious injections");
        return processKey;
    }
}
