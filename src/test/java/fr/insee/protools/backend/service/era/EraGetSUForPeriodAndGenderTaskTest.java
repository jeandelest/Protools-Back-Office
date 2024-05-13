package fr.insee.protools.backend.service.era;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.dto.era.CensusJsonDto;
import fr.insee.protools.backend.dto.era.GenderType;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.test.FlowableTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.utils.FlowableVariableUtils.getMissingVariableMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FlowableTest
class EraGetSUForPeriodAndGenderTaskTest extends TestWithContext {

    @Mock EraService eraService;
    @InjectMocks EraGetSUForPeriodAndGenderTask eraGetSUForPeriodAndGenderTask;

    String dumyId = "ID999";
    final static String era_context_2partition = "{ \"partitions\": [{ \"id\": 1  ,  \"sexe\": \"hommes\" }, { \"id\": 2  ,  \"sexe\": \"femmes\" }]   }";

    @Test
    void execute_should_throwError_when_null_context(){
        assertThat_delegate_throwError_when_null_context(eraGetSUForPeriodAndGenderTask);
    }

    @Test
    @DisplayName("Test execute method - should throw if VARNAME_ERA_QUERY_START_DATE or VARNAME_ERA_QUERY_END_DATE or VARNAME_CURRENT_PARTITION_ID not initialized")
    void execute_should_throw_FlowableIllegalArgumentException_when_variables_notDefined() {
        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);
        initContexteMockWithString(era_context_2partition);

        //Execute the unit under test
        FlowableIllegalArgumentException exception = assertThrows(FlowableIllegalArgumentException.class, () -> eraGetSUForPeriodAndGenderTask.execute(execution));
        //Post conditions
        assertThat(exception.getMessage()).isEqualTo(getMissingVariableMessage(VARNAME_ERA_QUERY_START_DATE));

        //Create First variable
        when(execution.getVariable(VARNAME_ERA_QUERY_START_DATE, LocalDate.class)).thenReturn(LocalDate.now().minusDays(1));
        //Execute again
        exception = assertThrows(FlowableIllegalArgumentException.class, () -> eraGetSUForPeriodAndGenderTask.execute(execution));
        //Check the error
        assertThat(exception.getMessage()).isEqualTo(getMissingVariableMessage(VARNAME_ERA_QUERY_END_DATE));

        //Create 2nd variable
        when(execution.getVariable(VARNAME_ERA_QUERY_END_DATE, LocalDate.class)).thenReturn(LocalDate.now());
        //Execute again
        exception = assertThrows(FlowableIllegalArgumentException.class, () -> eraGetSUForPeriodAndGenderTask.execute(execution));
        //Check the error
        assertThat(exception.getMessage()).isEqualTo(getMissingVariableMessage(VARNAME_CURRENT_PARTITION_ID));

        //Create 3rd variable
        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(1l);
        //Execute again
        assertDoesNotThrow(() -> eraGetSUForPeriodAndGenderTask.execute(execution));
    }

    @Test
    @DisplayName("Test execute method - should throw if Context is not correct")
    void execute_should_throw_BadContext_when_contextIncorrect() throws JsonProcessingException {
        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);

        LocalDate startDate = LocalDate.now().minusDays(1), endDate = LocalDate.now();
        when(execution.getVariable(VARNAME_ERA_QUERY_START_DATE, LocalDate.class)).thenReturn(startDate);
        when(execution.getVariable(VARNAME_ERA_QUERY_END_DATE, LocalDate.class)).thenReturn(endDate);
        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(1l);

        final String context1 =
                "{ \"partitions\": [{ \"id\": 1  }]   }";
        final String context2 =
                "{ \"partitions\": [{ \"id\": 1  ,  \"sexe\": \"3\" }]   }";
        final String context3 =
                "{ \"partitions\": [{ \"id\": 1  ,  \"sexe\": \"1\" }]   }";
        final String context4 =
                "{ \"partitions\": [{ \"id\": 1  ,  \"sexe\": 3 }]   }";
        List<String> contextErrorTestList=List.of(context1,context2,context3,context4);
        for(String context : contextErrorTestList){
            //Precondition
            ProtoolsTestUtils.initContexteMockFromString(protoolsContext, context);
            //Run test
            assertThrows(BadContextIncorrectBPMNError.class, () -> eraGetSUForPeriodAndGenderTask.execute(execution));
            Mockito.reset(protoolsContext);
        }
        //TEST OK
        //Precondition
        ProtoolsTestUtils.initContexteMockFromString(protoolsContext, era_context_2partition);
        //Run test
        assertDoesNotThrow(() -> eraGetSUForPeriodAndGenderTask.execute(execution));
        Mockito.reset(protoolsContext);
    }

    @Test
    @DisplayName("Test execute method - should work and make correct call to service and store result in correct variable")
    void execute_should_work_when_variables_OK() {
        //Precondition
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);
        LocalDate startDate = LocalDate.now().minusDays(1), endDate = LocalDate.now();
        initContexteMockWithString(era_context_2partition);

        when(execution.getVariable(VARNAME_ERA_QUERY_START_DATE, LocalDate.class)).thenReturn(startDate);
        when(execution.getVariable(VARNAME_ERA_QUERY_END_DATE, LocalDate.class)).thenReturn(endDate);
        when(execution.getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class)).thenReturn(1l);

        GenderType gender = GenderType.MALE;
        Long eraId1 = 500l, eraId2 = 1000l;
        CensusJsonDto eraSU1 = CensusJsonDto.builder()
                .id(eraId1)
                .build();
        CensusJsonDto eraSU2 = CensusJsonDto.builder()
                .id(eraId2)
                .build();
        List<CensusJsonDto> listOfEraSU = List.of(eraSU1, eraSU2);
        when(eraService.getSUForPeriodAndSex(startDate, endDate, gender)).thenReturn(listOfEraSU);

        //Run method under test
        assertDoesNotThrow(() -> eraGetSUForPeriodAndGenderTask.execute(execution));

        //Post conditions
        verify(eraService, times(1)).getSUForPeriodAndSex(startDate, endDate, gender);
        verify(execution, times(1)).setVariableLocal(VARNAME_ERA_RESPONSE, listOfEraSU);
    }
}