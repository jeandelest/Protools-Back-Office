package com.protools.flowableDemo.model.notifications;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum NotificationType {
    ERROR("error"),
    WARNING("warning"),
    INFO("info"),
    SUCCESS("success");

    private String id;

}
