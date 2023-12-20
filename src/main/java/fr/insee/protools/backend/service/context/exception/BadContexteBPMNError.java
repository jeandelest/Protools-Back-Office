package fr.insee.protools.backend.service.context.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

/**
 * Parent of all Exceptions while reading protools context
 * disable warning on inheritance too depp
 */
@SuppressWarnings("squid:S110")
public abstract class BadContexteBPMNError extends ProtoolsBpmnError {
    protected BadContexteBPMNError(String errorCode, String message) {
        super(errorCode, message);
    }
}
