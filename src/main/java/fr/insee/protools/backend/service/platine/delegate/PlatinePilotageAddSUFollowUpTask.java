package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_CAMPAGNE_ID;
import static fr.insee.protools.backend.service.platine.utils.PlatineHelper.computePilotagePartitionID;

@Slf4j
@Component
public class PlatinePilotageAddSUFollowUpTask implements JavaDelegate, DelegateContextVerifier {

    @Autowired ContextService protoolsContext;
    @Autowired PlatinePilotageService platinePilotageService;


    @Override
    public void execute(DelegateExecution execution) {
        //Contexte
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();
        //Process variables
        Long suId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_SURVEY_UNIT_IDENTIFIER, Long.class);
        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        log.info("ProcessInstanceId={} - currentPartitionId={}  suId={} begin",execution.getProcessInstanceId(), currentPartitionId,suId);

        //Post a follow-up state to this SU
        String platinePartitionId = computePilotagePartitionID(campainId,currentPartitionId);
        platinePilotageService.addFollowUpState(suId, platinePartitionId);

        //Unset the follow-up variable
        execution.getParent().removeVariableLocal(VARNAME_SU_IS_TO_FOLLOWUP);
        log.info("ProcessInstanceId={} - currentPartitionId={} - suId={}  end"
                ,execution.getProcessInstanceId(), currentPartitionId,suId);
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of("Context is missing");
        }
        return DelegateContextVerifier.computeMissingChildrenMessages(Set.of(CTX_CAMPAGNE_ID),contextRootNode,getClass());
    }
}