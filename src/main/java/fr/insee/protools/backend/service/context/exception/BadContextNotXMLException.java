package fr.insee.protools.backend.service.context.exception;

/**
 * Runtime exception indicating the context is not an XML File
 */
public class BadContextNotXMLException extends RuntimeException {

        public BadContextNotXMLException(String message, Throwable cause) {
                super(message, cause);
        }

        public BadContextNotXMLException(String message) {
                super(message);
        }

}
