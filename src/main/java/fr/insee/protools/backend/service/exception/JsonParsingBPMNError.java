package fr.insee.protools.backend.service.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class JsonParsingBPMNError extends BpmnError {
    public JsonParsingBPMNError(String message, Throwable cause) {
        super(BPMNERROR_CODE_DEFAULT, message+" Exception: "+cause.getMessage());
    }
}
