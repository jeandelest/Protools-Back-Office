package fr.insee.protools.backend.service.context.exception;

import org.flowable.engine.delegate.BpmnError;

/**
 * Parent of all Exceptions while reading protools context
 */
public abstract class BadContexteBPMNError extends BpmnError {
    public BadContexteBPMNError(String errorCode, String message) {
        super(errorCode, message);
    }
}
