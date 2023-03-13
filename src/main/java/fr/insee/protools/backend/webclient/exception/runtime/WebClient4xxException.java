package fr.insee.protools.backend.webclient.exception.runtime;

import org.springframework.http.HttpStatusCode;

public class WebClient4xxException extends RuntimeException {

    private HttpStatusCode errorCode;
    public WebClient4xxException(String message, HttpStatusCode errorCode) {
        super(message);
        this.errorCode=errorCode;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }


}
