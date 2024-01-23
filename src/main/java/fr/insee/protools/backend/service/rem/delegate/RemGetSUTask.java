package fr.insee.protools.backend.service.rem.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.rem.RemService;
import fr.insee.protools.backend.dto.rem.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT_IDENTIFIER;

@Slf4j
@Component
public class RemGetSUTask implements JavaDelegate, DelegateContextVerifier {

    @Autowired RemService remService;
    @Autowired ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        //No need context

        //Get the UE
        Long suId = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_REM_SURVEY_UNIT_IDENTIFIER, Long.class);
        log.info("ProcessInstanceId={} - suId={}",execution.getProcessInstanceId(), suId);
        //TODO : ne pas créer le DTO ici : Ca ne sert à rien...
        REMSurveyUnitDto remSurveyUnitDto = remService.getSurveyUnit(suId);
        execution.setVariableLocal(VARNAME_REM_SURVEY_UNIT,objectMapper.valueToTree(remSurveyUnitDto));
        log.trace("suId={} - content={}", suId, remSurveyUnitDto);
        log.debug("ProcessInstanceId={} - suId={} end",execution.getProcessInstanceId(), suId);
    }
}