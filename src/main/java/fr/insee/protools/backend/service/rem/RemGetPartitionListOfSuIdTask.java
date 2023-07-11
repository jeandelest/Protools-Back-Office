package fr.insee.protools.backend.service.rem;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SU_ID_LIST;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITIONS;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITION_ID;

@Slf4j
@Component
public class RemGetPartitionListOfSuIdTask implements JavaDelegate, DelegateContextVerifier {

    @Autowired RemService remService;
    @Autowired ContextService protoolsContext;

    @Override
    public void execute(DelegateExecution execution) {
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);

        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_CURRENT_PARTITION_ID, String.class);
        log.info("ProcessInstanceId={} - partition={} begin",execution.getProcessInstanceId(),currentPartitionId);

        Long[] partitionSUIds = remService.getSampleSuIds(currentPartitionId);
        List<Long> remSuIdList = List.of(partitionSUIds);
        execution.getParent().setVariableLocal(VARNAME_REM_SU_ID_LIST, remSuIdList);
        log.debug("ProcessInstanceId={} -  partition={} - remSuIdList={}",execution.getProcessInstanceId(),currentPartitionId,remSuIdList);

        log.info("ProcessgInstanceId={} - partition={} end",execution.getProcessInstanceId(),currentPartitionId);
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of("Context is missing");
        }
        Set<String> results=new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_PARTITIONS
                );
        Set<String> requiredPartition =
                Set.of(CTX_PARTITION_ID);
        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));
        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();
        //Partitions
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));
        }
        return results;
    }
}