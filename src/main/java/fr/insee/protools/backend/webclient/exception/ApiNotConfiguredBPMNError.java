package fr.insee.protools.backend.webclient.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class ApiNotConfiguredBPMNError extends BpmnError {
    public ApiNotConfiguredBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
