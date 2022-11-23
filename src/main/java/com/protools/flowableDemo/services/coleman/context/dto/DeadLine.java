package com.protools.flowableDemo.services.coleman.context.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.protools.flowableDemo.services.coleman.context.TimeUtility;
import com.protools.flowableDemo.services.coleman.context.enums.DeadLineType;
import lombok.Data;

import java.time.Duration;
import java.time.format.DateTimeParseException;

@Data
public class DeadLine {
    private DeadLineType type;

    @JacksonXmlText
    private String value;

    public boolean isValid() {
        return (type == DeadLineType.date && isValidDate()) || (type == DeadLineType.duree && isValidDuration());
    }

    private boolean isValidDate() {
        try {
            TimeUtility.parseToZonedDateTime(value);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }

    private boolean isValidDuration() {
        try {
            Duration.parse(value);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }
}
