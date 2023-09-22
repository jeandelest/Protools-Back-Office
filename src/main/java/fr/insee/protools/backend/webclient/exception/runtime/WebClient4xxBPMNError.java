package fr.insee.protools.backend.webclient.exception.runtime;

import org.flowable.engine.delegate.BpmnError;
import org.springframework.http.HttpStatusCode;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class WebClient4xxBPMNError extends BpmnError {

    private final HttpStatusCode httpStatusCodeError;

    public WebClient4xxBPMNError(String message, HttpStatusCode httpStatusCodeError) {
        super(BPMNERROR_CODE_DEFAULT, message);
        this.httpStatusCodeError = httpStatusCodeError;
    }

    public HttpStatusCode getHttpStatusCodeError() {
        return httpStatusCodeError;
    }


}
