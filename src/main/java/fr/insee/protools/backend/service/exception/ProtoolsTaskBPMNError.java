package fr.insee.protools.backend.service.exception;

import org.flowable.engine.delegate.BpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

//TODO : Créer une classe mère de toutes nos expcetions pour l'attrapper dans les controller advice
public class ProtoolsTaskBPMNError extends BpmnError {
    public ProtoolsTaskBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT,message);
    }
}
