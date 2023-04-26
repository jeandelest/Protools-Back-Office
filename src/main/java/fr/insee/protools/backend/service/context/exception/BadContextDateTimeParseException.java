package fr.insee.protools.backend.service.context.exception;

public class BadContextDateTimeParseException extends RuntimeException{
    public BadContextDateTimeParseException(String message) {
        super(message);
    }

}
