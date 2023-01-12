package com.protools.flowableDemo.model.notifications;

import lombok.*;
//TODO: add persistence arguments (id generated value, table, etc...)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {

    private String message;
    private String date;
    private String TaskName;
    private String ProcessName;
    private NotificationType type;


}
