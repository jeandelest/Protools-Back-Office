package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.utils.PlatineHelper;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.test.FlowableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_CAMPAGNE_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FlowableTest
class PlatinePilotageAddSUFollowUpTaskTest extends TestWithContext {

    final static String ressourceFolder = ClassUtils.convertClassNameToResourcePath(PlatinePilotageAddSUFollowUpTaskTest.class.getPackageName());
    final static String platine_context_json = ressourceFolder + "/protools-contexte-platine-individu.json";
    final static String platine_context_incorrect_json = ressourceFolder + "/protools-contexte-platine-incorrect-no-campaign-id.json";

    @Mock PlatinePilotageService platinePilotageService;
    @InjectMocks PlatinePilotageAddSUFollowUpTask platinePilotageTask;

    @Test
    void execute_should_throwError_when_null_context(){
        assertThat_delegate_throwError_when_null_context(platinePilotageTask);
    }

    @Test
    void execute_should_throw_BadContextIncorrectException_when_noContext() {
        DelegateExecution execution = createMockedExecution();
        //Execute the unit under test
        assertThrows(BadContextIncorrectBPMNError.class, () -> platinePilotageTask.execute(execution));
    }

    @Test
    void execute_should_throw_BadContextIncorrectException_when_CampaignId_missing() {
        DelegateExecution execution = createMockedExecution();
        initContexteMockWithFile(platine_context_incorrect_json);

        //Execute the unit under test
        assertThrows(BadContextIncorrectBPMNError.class, () -> platinePilotageTask.execute(execution));
    }

    @Test
    void execute_should_work_when_context_and_variables_OK() {
        DelegateExecution execution = createMockedExecution();
        //Prepare parent
        DelegateExecution executionParent = mock(DelegateExecution.class);
        doReturn(executionParent).when(execution).getParent();

        JsonNode contextRootNode = initContexteMockWithFile(platine_context_json);
        Long suId = 999L;
        Long currentPartitionID = 7777L;
        String platinePartionId = PlatineHelper.computePilotagePartitionID(contextRootNode.path(CTX_CAMPAGNE_ID).asText(), currentPartitionID);

        //Set a variable that should be un-set after execute method (value is not used by this method)
        lenient().doReturn(Boolean.FALSE).when(execution).getVariable(VARNAME_SU_IS_TO_FOLLOWUP, Boolean.class);

        //Execute the unit under test

        //No variable set ==> Error
        assertThrows(FlowableIllegalArgumentException.class, () -> platinePilotageTask.execute(execution));

        //set one variable ==> Error
        lenient().doReturn(suId).when(execution).getVariable(VARNAME_REM_SURVEY_UNIT_IDENTIFIER, Long.class);
        //Execute the unit under test
        assertThrows(FlowableIllegalArgumentException.class, () -> platinePilotageTask.execute(execution));

        //set 2nd variable ==> Success
        lenient().doReturn(currentPartitionID).when(execution).getVariable(VARNAME_CURRENT_PARTITION_ID, Long.class);
        //Execute the unit under test
        assertDoesNotThrow(() -> platinePilotageTask.execute(execution));

        //Post conditions :
        // 1 : Exactly one call to post platinePilotageService.addFollowUpState with correct parameters
        verify(platinePilotageService, times(1)).addFollowUpState(suId, platinePartionId);
        //2 : VARNAME_SU_IS_TO_FOLLOWUP has been removed
        verify(executionParent, times(1)).removeVariableLocal(VARNAME_SU_IS_TO_FOLLOWUP);
    }

}