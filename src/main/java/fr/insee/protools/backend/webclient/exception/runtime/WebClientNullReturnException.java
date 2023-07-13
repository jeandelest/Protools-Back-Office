package fr.insee.protools.backend.webclient.exception.runtime;

public class WebClientNullReturnException extends RuntimeException{
    public WebClientNullReturnException(String message) {
        super(message);
    }
}
