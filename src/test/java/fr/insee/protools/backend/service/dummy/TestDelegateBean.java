package fr.insee.protools.backend.service.dummy;

import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SU_ID_LIST;


@Component
class TestDelegateBean implements JavaDelegate {

    @Autowired DummyService dummyService;

    @Override
    public void execute(DelegateExecution execution) {
        List<Long> lot_ue = execution.getVariable("lot_ue", List.class);
        execution.getParent().setVariableLocal(VARNAME_REM_SU_ID_LIST, lot_ue);

        List<Long> remSuIdList = List.of(589811l, 589812l, 589813l);
        Long currentPartitionId = 70l;
        execution.getParent().setVariable(VARNAME_REM_SU_ID_LIST, remSuIdList);
        execution.getParent().setVariable(VARNAME_CURRENT_PARTITION_ID, currentPartitionId);

        List<Long> longList = Arrays.stream("input".split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

    }
}