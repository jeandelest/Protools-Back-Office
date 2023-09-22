package fr.insee.protools.backend.webclient.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class KeycloakTokenConfigUncheckedBPMNError extends BpmnError {

    public KeycloakTokenConfigUncheckedBPMNError(Exception e) {
        super(BPMNERROR_CODE_DEFAULT, e.getMessage());
    }
}
