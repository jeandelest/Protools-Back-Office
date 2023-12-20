package fr.insee.protools.backend.service.context.exception;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

/**
 * Runtime exception indicating the context is not correct
 * disable warning on inheritance too depp
 */
@SuppressWarnings("squid:S110")
public class BadContextIncorrectBPMNError extends BadContexteBPMNError {
    public BadContextIncorrectBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
