package fr.insee.protools.backend.service.context.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

/**
 * Runtime exception indicating the context is not a JSON File
 */
public class BadContextNotJSONBPMNError extends BpmnError {

    public BadContextNotJSONBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
