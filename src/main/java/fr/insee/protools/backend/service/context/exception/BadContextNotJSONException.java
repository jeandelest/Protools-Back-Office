package fr.insee.protools.backend.service.context.exception;

/**
 * Runtime exception indicating the context is not a JSON File
 */
public class BadContextNotJSONException extends RuntimeException {

        public BadContextNotJSONException(String message, Throwable cause) {
                super(message, cause);
        }

        public BadContextNotJSONException(String message) {
                super(message);
        }

}
