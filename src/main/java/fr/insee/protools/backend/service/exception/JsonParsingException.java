package fr.insee.protools.backend.service.exception;

//TODO : Créer une classe mère de toutes nos expcetions pour l'attrapper dans les controller advice
public class JsonParsingException extends RuntimeException {
    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
