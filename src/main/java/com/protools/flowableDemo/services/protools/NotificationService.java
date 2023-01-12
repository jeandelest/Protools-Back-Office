package com.protools.flowableDemo.services.protools;

import com.protools.flowableDemo.model.exceptions.RessourceNotFoundException;
import com.protools.flowableDemo.model.notifications.Notification;
import com.protools.flowableDemo.model.notifications.NotificationType;
import com.protools.flowableDemo.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void saveNotification(String processName, String taskName, String message, NotificationType type, LocalDateTime date) {
        log.info("\t \t >> Saving notification for processName: {}, taskName: {}, message: {}, type: {}", processName, taskName, message, type);
        notificationRepository.save(new Notification(processName, date, taskName, message, type));
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public List<Notification> findAllByProcessName(String processName) {
        return notificationRepository.findAllByProcessName(processName);
    }
    public List<Notification> findAllByTaskName(String taskName) throws RessourceNotFoundException{
        return notificationRepository.findAllByTaskName(taskName);
    }

    public List<Notification> findAllByType(NotificationType type) throws RessourceNotFoundException{
        return notificationRepository.findAllByType(type);
    }

    public List<Notification> findAllOrderByDateDesc() {
        return notificationRepository.findAllOrderByDateDesc();
    }


}
