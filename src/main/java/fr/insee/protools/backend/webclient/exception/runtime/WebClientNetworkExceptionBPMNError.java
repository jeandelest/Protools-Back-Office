package fr.insee.protools.backend.webclient.exception.runtime;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.BpmnError;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

@Slf4j
public class WebClientNetworkExceptionBPMNError extends BpmnError {

    private final WebClientRequestException ex;

    public WebClientNetworkExceptionBPMNError(WebClientRequestException ex) {
        super(BPMNERROR_CODE_DEFAULT, computeMessage(ex));
        this.ex = ex;
        log.error(this.getMessage());
    }

    public WebClientRequestException getSourceException() {
        return ex;
    }

    private static String computeMessage(WebClientRequestException ex){
        return String.format("WebClientNetworkExceptionBPMNError : class=[%s] - method=[%s] - uri=[%s] message=[%s] - root_cause=[%s] - root_message",
                ex.getClass(),
                ex.getMethod(),
                ex.getUri(),
                ex.getMessage(),
                ex.getMostSpecificCause().getClass(),
                ex.getMostSpecificCause().getMessage());
    }
}
