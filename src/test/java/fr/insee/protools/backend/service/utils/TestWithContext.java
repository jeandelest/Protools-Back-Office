package fr.insee.protools.backend.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.ContextService;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class TestWithContext {

    @Spy protected ContextService protoolsContext;

    protected final String dumyId="ID1";

    protected JsonNode initContexteMockWithFile(String contexteToLoad){
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(contexteToLoad);
        when(protoolsContext.getContextByProcessInstance(anyString())).thenReturn(contextRootNode);
        return contextRootNode;
    }

    protected JsonNode initContexteMockWithString(String contexteAsString){
        try{
            JsonNode contextRootNode = new ObjectMapper().readTree(contexteAsString);
            when(protoolsContext.getContextByProcessInstance(anyString())).thenReturn(contextRootNode);
            return  contextRootNode;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public DelegateExecution createMockedExecution(){
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceId()).thenReturn(dumyId);
        return execution;
    }
}
