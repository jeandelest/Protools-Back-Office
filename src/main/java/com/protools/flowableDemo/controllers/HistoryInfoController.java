package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.engineService.HistoryInfoService;
import io.swagger.v3.oas.annotations.Operation;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class HistoryInfoController {
    @Autowired
    private HistoryInfoService historyInfoService;

    @CrossOrigin
    @Operation(summary = "Get Process History")
    @GetMapping(value = "/history/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoricProcessInstance>> getHistoryProcess(){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getHistoryProcess()));
    }

    @CrossOrigin
    @Operation(summary = "Get Tasks History by ProcessDefinitionID")
    @GetMapping(value = "/history/task/{ProcessDefinitionID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoricTaskInstance>> getHistoryTask(@PathVariable String ProcessDefinitionID){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getHistoryTask(ProcessDefinitionID)));
    }

    @CrossOrigin
    @Operation(summary = "Get Activity History")
    @GetMapping(value = "/history/activity", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoricActivityInstance>> getHistoryActivity(){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getHistoryActivity()));
    }

    @CrossOrigin
    @Operation(summary = "Get DeadLetter Jobs")
    @GetMapping(value = "/history/deadLetter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getDeadJobs(){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getFailedJobs()));
    }

    @CrossOrigin
    @Operation(summary = "Get Suspended Jobs")
    @GetMapping(value = "/history/suspended", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getSuspendedJobs(){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getSuspendedJobs()));
    }

    @CrossOrigin
    @Operation(summary = "Get deleted Processes")
    @GetMapping(value = "/history/deleted", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HistoricProcessInstance>> getDeletedProcesses(){
        return ResponseEntity.status(HttpStatus.OK).body((historyInfoService.getDeletedProcess()));
    }

}
