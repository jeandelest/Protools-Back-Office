package fr.insee.protools.backend.service.context.exception;

/**
 * Runtime exception indicating an IOException while reading context
 */
public class BadContextIOException extends RuntimeException {

        public BadContextIOException(String message, Throwable cause) {
                super(message+" - message="+cause.getMessage());
        }
}
