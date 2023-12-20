package fr.insee.protools.backend.service.exception;

import org.flowable.common.engine.api.FlowableException;

public class VariableClassCastException extends FlowableException {
    public VariableClassCastException(String message) {
        super(message);
    }
}
