package fr.insee.protools.backend.client.exception;

public class KeycloakTokenConfigUncheckedException extends  RuntimeException{

        public KeycloakTokenConfigUncheckedException(Exception e) {
                super(e);
        }
}
