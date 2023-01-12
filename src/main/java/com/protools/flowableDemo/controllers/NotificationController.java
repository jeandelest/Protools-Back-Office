package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.model.exceptions.FileNotFoundException;
import com.protools.flowableDemo.model.notifications.Notification;
import com.protools.flowableDemo.model.notifications.NotificationType;
import com.protools.flowableDemo.services.protools.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @CrossOrigin
    @Operation(summary = "Get all notifications from past events")
    @GetMapping(value = "/notifications", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotifications() {
        log.info("\t >> Getting all notifications from past events");
        return new ResponseEntity<>(notificationService.findAll(), HttpStatus.OK);

    }

    @CrossOrigin
    @Operation(summary = "Get all notifications sorted by date descending")
    @GetMapping(value = "/notifications/sorted", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotificationsSorted() {
        log.info("\t >> Getting all notifications sorted by date descending");
        return new ResponseEntity<>(notificationService.findAllOrderByDateDesc(), HttpStatus.OK);

    }

    @CrossOrigin
    @Operation(summary= "Get all notifications by process name")
    @GetMapping(value = "/notifications/process/{processName}", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotificationsByProcessName(@RequestParam("processName") String processName) {
        log.info("\t >> Getting all notifications by process name");
        return new ResponseEntity<>(notificationService.findAllByProcessName(processName), HttpStatus.OK);

    }

    @CrossOrigin
    @Operation(summary= "Get all notifications by task name")
    @GetMapping(value = "/notifications/task/{taskName}", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotificationsByTaskName(@RequestParam("taskName") String taskName) {
        log.info("\t >> Getting all notifications by task name");
        return new ResponseEntity<>(notificationService.findAllByTaskName(taskName), HttpStatus.OK);

    }

    @CrossOrigin
    @Operation(summary= "Get all notifications by notification type")
    @GetMapping(value = "/notifications/type/{type}", produces = "application/json")
    public ResponseEntity<List<Notification>> getNotificationsByType(@RequestParam("type") NotificationType type) {
        log.info("\t >> Getting all notifications by notification type");
        return new ResponseEntity<>(notificationService.findAllByType(type), HttpStatus.OK);

    }
}
