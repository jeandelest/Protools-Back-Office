package fr.insee.protools.backend.webclient.exception.runtime;

public class WebClient5xxException extends RuntimeException {
    public WebClient5xxException(String message) {
        super(message);
    }

}
