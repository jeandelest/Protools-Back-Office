package fr.insee.protools.backend.service.context.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

/**
 * Runtime exception indicating the context is not a JSON File
 */
public class BadContextNotJSONBPMNError extends ProtoolsBpmnError {

    public BadContextNotJSONBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
