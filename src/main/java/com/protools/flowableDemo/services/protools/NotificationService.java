package com.protools.flowableDemo.services.protools;

import com.protools.flowableDemo.model.exceptions.RessourceNotFoundException;
import com.protools.flowableDemo.model.notifications.Notification;
import com.protools.flowableDemo.model.notifications.NotificationType;
import com.protools.flowableDemo.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void saveNotification(String message, NotificationType type) {

        log.info("\t >> Saving notification for, message: {}, type: {}", message, type.getId());
        LocalDateTime lt
                = LocalDateTime.now();
        notificationRepository.save(new Notification(message, lt, type.getId()));
    }

    public void saveNotification(String message, String taskName, NotificationType type) {

        log.info("\t \t >> Saving notification for taskName: {}, message: {}, type: {}", taskName, message, type.getId());
        LocalDateTime lt
                = LocalDateTime.now();
        notificationRepository.save(new Notification(message, lt, taskName , type.getId()));
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public List<Notification> findAllByTaskName(String taskName) throws RessourceNotFoundException{
        return notificationRepository.findAllByTaskName(taskName);
    }

    public List<Notification> findAllByType(NotificationType type) throws RessourceNotFoundException{
        return notificationRepository.findAllByType(type);
    }

    public List<Notification> findAllOrderByDateDesc() {
        return notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }


}
