package fr.insee.protools.backend.webclient.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class KeycloakTokenConfigBPMNError extends BpmnError {

    public KeycloakTokenConfigBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}