package fr.insee.protools.backend.flowcontrol;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Service
@Slf4j
public class FlowcontrolIsSUToFollowUp implements JavaDelegate, DelegateContextVerifier {

    @Autowired ContextService protoolsContext;

    @Override
    public void execute(DelegateExecution execution) {
        Boolean isToFollowUp = false;

        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        checkContextOrThrow(log, execution.getProcessInstanceId(), contextRootNode);

        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, Long.class);
        //        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);

        Triple<Instant, Long, Long> suItem = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_SU_CREATION_ITEM, Triple.class);
        log.info("ProcessInstanceId={} - suItem={}  begin", execution.getProcessInstanceId(), suItem);

        Instant openingInstant = suItem.getLeft();
        //TODO : à enlever ; mettre de la conf (période ou date fixe) sur la partition et récupérer la conf de la partition
        Instant dateRelance = openingInstant.plusSeconds(3 * 60l);

        if (openingInstant != null && dateRelance.isBefore(Instant.now())) {
            isToFollowUp = true;
        }
        execution.getParent().setVariableLocal(VARNAME_SU_IS_TO_FOLLOWUP, isToFollowUp);

        //Extract SU id and partition id as it will be used in following tasks
        execution.getParent().setVariableLocal(VARNAME_REM_SURVEY_UNIT_IDENTIFIER, suItem.getRight());
        execution.getParent().setVariableLocal(VARNAME_CURRENT_PARTITION_ID, suItem.getMiddle());
        log.info("ProcessInstanceId={} - suItem={}  - isToFollowUp={} end", execution.getProcessInstanceId(), suItem, isToFollowUp);
    }
}
