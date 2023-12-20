package fr.insee.protools.backend.webclient.exception.runtime;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class WebClient5xxBPMNError extends ProtoolsBpmnError {
    public WebClient5xxBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
