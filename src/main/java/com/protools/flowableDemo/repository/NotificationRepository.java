package com.protools.flowableDemo.repository;

import com.protools.flowableDemo.model.notifications.Notification;
import com.protools.flowableDemo.model.notifications.NotificationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findAllByTaskName(String taskName);
    List<Notification> findAll();
    List<Notification> findAllByType(NotificationType type);
}

