package fr.insee.protools.backend.controller.requests;

import lombok.Data;
import org.flowable.rest.service.api.engine.variable.RestVariable;

import java.util.List;

@Data
public class ProtoolsProcessInstanceWithContextCreateRequest {
    List<RestVariable> variables;
}