package fr.insee.protools.backend.service.exception;

import com.fasterxml.jackson.databind.JsonNode;

public class IncorrectSUException extends RuntimeException {
    public String getRemSU() {
        return remSU;
    }

    private final String remSU;

    public IncorrectSUException(String message, JsonNode remSUNode, Exception e) {
        super(message, e);
        if (remSUNode == null) {
            remSU = "null node";
        } else {
            this.remSU = remSUNode.toString();
        }
    }

    public IncorrectSUException(String message, JsonNode remSUNode) {
        super(message);
        if (remSUNode == null) {
            remSU = "null node";
        } else {
            this.remSU = remSUNode.toString();
        }
    }

    public IncorrectSUException(String message) {
        super(message);
        remSU = "";
    }
}