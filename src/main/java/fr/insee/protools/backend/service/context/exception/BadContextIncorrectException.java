package fr.insee.protools.backend.service.context.exception;

/**
 * Runtime exception indicating the context is not correct
 */
public class BadContextIncorrectException extends RuntimeException {

        public BadContextIncorrectException(String message, Throwable cause) {
                super(message, cause);
        }

        public BadContextIncorrectException(String message) {
                super(message);
        }

}
