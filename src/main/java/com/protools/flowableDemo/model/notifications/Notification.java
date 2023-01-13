package com.protools.flowableDemo.model.notifications;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import javax.persistence.Entity;
//TODO: add persistence arguments (id generated value, table, etc...)

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "UUID", sequenceName = "UUID", allocationSize = 1)
    private Long id;
    private String message;
    private LocalDateTime date;
    private String taskName;
    private String type;

    public Notification(String message, LocalDateTime date, String type) {
        this.message = message;
        this.date = date;
        this.type = type;
    }
    public Notification(String message, LocalDateTime date, String taskName, String type) {
        this.message = message;
        this.date = date;
        this.taskName = taskName;
        this.type = type;
    }
}
