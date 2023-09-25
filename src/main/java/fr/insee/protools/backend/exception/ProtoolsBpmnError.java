package fr.insee.protools.backend.exception;

import org.flowable.engine.delegate.BpmnError;

public class ProtoolsBpmnError extends BpmnError {
    public ProtoolsBpmnError(String errorCode) {
        super(errorCode);
    }

    public ProtoolsBpmnError(String errorCode, String message) {
        super(message,errorCode);
    }
}
