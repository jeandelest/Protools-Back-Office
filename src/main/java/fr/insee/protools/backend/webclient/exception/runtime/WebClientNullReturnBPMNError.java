package fr.insee.protools.backend.webclient.exception.runtime;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class WebClientNullReturnBPMNError extends BpmnError {
    public WebClientNullReturnBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
