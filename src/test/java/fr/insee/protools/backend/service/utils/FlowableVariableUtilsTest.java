package fr.insee.protools.backend.service.utils;

import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.exception.VariableClassCastException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlowableVariableUtilsTest {

    @Test
    void getVariableOrThrow() {
        //Prepare
        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        String key="key";
        String key2="key";

        Long   longVal = 55l;

        doReturn(longVal).when(delegateExecution).getVariable(key,Long.class);
        doReturn(null).when(delegateExecution).getVariable(key2,Long.class);

        doThrow(new ClassCastException("String cannot be converted to Long")).when(delegateExecution).getVariable(key,String.class);

        //Call methods under tests
        assertDoesNotThrow(()->delegateExecution.getVariable(key,Long.class));
        assertThrows(ClassCastException.class, () -> delegateExecution.getVariable(key,String.class));
        assertThrows(VariableClassCastException.class, () -> FlowableVariableUtils.getVariableOrThrow(delegateExecution,key,String.class););
        assertThrows(FlowableIllegalArgumentException.class, () -> FlowableVariableUtils.getVariableOrThrow(delegateExecution,key2,Long.class););
        //Post status
    }



    @Test
    void getMissingVariableMessage() {
    }
}