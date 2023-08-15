package fr.insee.protools.backend.service.exception;

//TODO : Créer une classe mère de toutes nos expcetions pour l'attrapper dans les controller advice
public class ProtoolsTaskException extends RuntimeException {
    public ProtoolsTaskException(String message) {
        super(message);
    }
}
