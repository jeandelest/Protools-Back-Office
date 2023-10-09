package fr.insee.protools.backend.service.context.exception;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

@SuppressWarnings("squid:S110")
public class BadContextDateTimeParseBPMNError extends BadContexteBPMNError {
    public BadContextDateTimeParseBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
