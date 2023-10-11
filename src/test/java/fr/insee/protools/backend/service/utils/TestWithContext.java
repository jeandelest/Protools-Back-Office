package fr.insee.protools.backend.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.ContextService;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public abstract class TestWithContext {

    @Spy protected ContextService protoolsContext;

    protected final String dumyId="ID1";

    protected JsonNode initContexteMockWithFile(String contexteToLoad){
        JsonNode contextRootNode = ProtoolsTestUtils.asJsonNode(contexteToLoad);
        doReturn(contextRootNode).when(protoolsContext).getContextByProcessInstance(anyString());
        return contextRootNode;
    }

    protected JsonNode initContexteMockWithString(String contexteAsString){
        try{
            return ProtoolsTestUtils.initContexteMockFromString(protoolsContext,contexteAsString);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public DelegateExecution createMockedExecution(){
        DelegateExecution execution = mock(DelegateExecution.class);
        doReturn(dumyId).when(execution).getProcessInstanceId();
        return execution;
    }
}
