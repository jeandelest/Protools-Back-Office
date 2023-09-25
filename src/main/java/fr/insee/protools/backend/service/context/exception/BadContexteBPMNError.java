package fr.insee.protools.backend.service.context.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

/**
 * Parent of all Exceptions while reading protools context
 */
public abstract class BadContexteBPMNError extends ProtoolsBpmnError {
    public BadContexteBPMNError(String errorCode, String message) {
        super(errorCode, message);
    }
}
