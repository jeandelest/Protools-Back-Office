package fr.insee.protools.backend.service.rem.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.rem.REMSurveyUnitDto;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.rem.RemService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemGetPartitionListOfSuTask implements JavaDelegate, DelegateContextVerifier {

    private final RemService remService;

    @Override
    public void execute(DelegateExecution execution) {
        Long currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_CURRENT_PARTITION_ID, Long.class);
        //No need protools context ==> no checkContextOrThrow
        log.info("ProcessInstanceId={} - partition={} begin",execution.getProcessInstanceId(),currentPartitionId);

        JsonNode[] partitionSUs = remService.getPartitionAllSU(currentPartitionId);
        List<JsonNode> remSUList = List.of(partitionSUs);
        log.trace("remSUList.length="+partitionSUs.length);
        execution.getParent().setVariableLocal(VARNAME_REM_SU_LIST, remSUList);
        log.debug("ProcessInstanceId={} -  partition={} - remSUList.size={} end",execution.getProcessInstanceId(),currentPartitionId,remSUList.size());
    }
}