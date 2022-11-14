package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.engineService.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

@RestController
@Slf4j
public class ProcessController {
    @Autowired
    private WorkflowService workflowService;

    @CrossOrigin
    @Operation(summary = "Start process by ProcessKey")
    @PostMapping(value= "/start-process/{processKey}/{businessKey}")
    public ResponseEntity<String> startProcessInstance(@PathVariable String processKey, @PathVariable String businessKey, @RequestBody HashMap<String,Object> variables){
        log.info("> Start the process: "+ processKey);
        JSONObject object = workflowService.startProcess(processKey,businessKey,variables);
        log.info(String.valueOf(object));
        return ResponseEntity.status(HttpStatus.OK).body((String.valueOf(object)));
    }
    @CrossOrigin
    @Operation(summary = "Claim all task by TaskID")
    @PostMapping("/get-tasks/{assignee}/{TaskID}")
    public ResponseEntity<Void> getTasks(@PathVariable String TaskID, @PathVariable String assignee) {
        log.info(">>> Claim assigned tasks for assignee "+ assignee+" <<<");
        workflowService.claimTasks(TaskID,assignee);
        return new ResponseEntity<>(HttpStatus.OK);

    }
    @CrossOrigin
    @Operation(summary = "Claim & Complete task by taskID, add variables to process")
    @PostMapping("/complete-task/{assignee}/{taskID}")
    public ResponseEntity<Void> completeTaskA(@PathVariable String taskID, @RequestBody HashMap<String,Object> variables, @PathVariable String assignee) {
        log.info(">>> Claim & Complete assigned task for assignee "+ assignee +" <<<");
        log.info("TaskID : "+taskID);
        workflowService.claimTasks(taskID,assignee);
        workflowService.completeTask(taskID,variables,assignee);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Delete Process Instance By Process ID")
    @PostMapping("/deleteProcess/{ProcessID}")
    public ResponseEntity<Void> deleteProcess(@PathVariable String ProcessID) {
        log.info(">>> Deleting ProcessInstance :" +ProcessID+" <<<");
        workflowService.deleteProcessInstance(ProcessID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Suspend Process Instance By Process ID")
    @PostMapping("/suspendProcess/{ProcessID}")
    public ResponseEntity<Void> suspendProcess(@PathVariable String ProcessID) {
        log.info(">>> Suspending ProcessInstance :" +ProcessID+" <<<");
        workflowService.suspendProcessInstance(ProcessID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Suspend Process Instance By Process ID")
    @PostMapping("/restart/{ProcessID}")
    public ResponseEntity<Void> restartProcess(@PathVariable String ProcessID) {
        log.info(">>> Activating ProcessInstance :" +ProcessID+" <<<");
        workflowService.restartProcessInstance(ProcessID);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @CrossOrigin
    @Operation(summary = "CancelProcess Instance By Process ID")
    @PostMapping("/cancelProcess/{ProcessID}/{reason}")
    public ResponseEntity<Void> cancelProcess(@PathVariable String ProcessID, @PathVariable String reason) {
        log.info(">>> Cancelling ProcessInstance :" +ProcessID+" <<<");
        workflowService.cancelProcessWithReason(ProcessID,reason);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Add BPMN file to deployment")
    @PostMapping("/addBPMN")
    public ResponseEntity<Void> deployBPMN() {
        log.info(">>> Adding BPMN to deployment <<<");
        workflowService.deployBpmnProcess();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Restart dead job by JobID")
    @PostMapping("/restartJob/{JobID}")
    public ResponseEntity<Void> restartJob(@PathVariable String JobID) {
        log.info(">>> Restarting Dead Job :" +JobID+" <<<");
        workflowService.relaunchDeadLetterJob(JobID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
