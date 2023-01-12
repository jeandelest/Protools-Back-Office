package com.protools.flowableDemo.model.notifications;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationType {
    ERROR("error"),
    WARNING("warning"),
    INFO("info"),
    SUCCESS("success");

    private String id;
}
