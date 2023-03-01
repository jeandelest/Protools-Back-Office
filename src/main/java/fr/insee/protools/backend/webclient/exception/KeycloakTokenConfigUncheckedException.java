package fr.insee.protools.backend.webclient.exception;

public class KeycloakTokenConfigUncheckedException extends  RuntimeException{

        public KeycloakTokenConfigUncheckedException(Exception e) {
                super(e);
        }
}
