package fr.insee.protools.backend.webclient.exception.runtime;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;
import org.springframework.http.HttpStatusCode;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class WebClient4xxBPMNError extends ProtoolsBpmnError {

    private final HttpStatusCode httpStatusCodeError;

    public WebClient4xxBPMNError(String message, HttpStatusCode httpStatusCodeError) {
        super(BPMNERROR_CODE_DEFAULT, message);
        this.httpStatusCodeError = httpStatusCodeError;
    }

    public HttpStatusCode getHttpStatusCodeError() {
        return httpStatusCodeError;
    }


}
