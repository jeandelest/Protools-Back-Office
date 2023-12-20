package fr.insee.protools.backend.service.context.resolvers;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CONTEXT_PARTITION_VARIABLES_BY_ID;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITION_DATE_DEBUT_COLLECTE;
import static fr.insee.protools.backend.service.context.ContextConstants.CTX_PARTITION_DATE_FIN_COLLECTE;

/**
 * Used to make protools context variables availables in BPMN expressions
 * exemple:
 *
 *   <intermediateCatchEvent id="id1" name="dummy">
 *     <timerEventDefinition>
 *       <timeDate>${partitionCtxResolver.getCollectionStartDate(execution,current_partition_id)}</timeDate>
 *     </timerEventDefinition>
 *   </intermediateCatchEvent>
 *
 *
 *   Flowable doc : https://documentation.flowable.com/latest/develop/be/be-expressions#customization
 */
@Component
public class PartitionCtxResolver {


    private Serializable getVariableOfPartition(ExecutionEntity execution, Long partitionId, String key) {
        HashMap<Long,HashMap<String, Serializable>> variablesByPartition = execution.getVariable(VARNAME_CONTEXT_PARTITION_VARIABLES_BY_ID, HashMap.class);
        HashMap<String, Serializable> partitionVariables = variablesByPartition.get(partitionId);
        if(partitionVariables==null) {
            throw new FlowableException("Could not get variable "+VARNAME_CONTEXT_PARTITION_VARIABLES_BY_ID+ " of partitionId="+partitionId);
        }
        return partitionVariables.get(key);
    }

    public Instant getCollectionStartDate(ExecutionEntity execution, Long partitionId) {
        return (Instant) getVariableOfPartition(execution,partitionId,CTX_PARTITION_DATE_DEBUT_COLLECTE);
    }


    public Instant getCollectionEndtDate(ExecutionEntity execution, Long partitionId) {
        return (Instant) getVariableOfPartition(execution,partitionId,CTX_PARTITION_DATE_FIN_COLLECTE);

    }
}