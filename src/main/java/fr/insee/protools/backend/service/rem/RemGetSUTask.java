package fr.insee.protools.backend.service.rem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.exception.VariableClassCastException;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        log.info("ProcessInstanceId={} - id={} begin",execution.getProcessInstanceId(), suId);
        //TODO : ne pas créer le DTO ici : Ca ne sert à rien...
        REMSurveyUnitDto remSurveyUnitDto = remService.getSurveyUnit(suId);
        execution.setVariableLocal(VARNAME_REM_SURVEY_UNIT,objectMapper.valueToTree(remSurveyUnitDto));
        log.info("Su id={} - content={}", suId, remSurveyUnitDto);
        log.info("ProcessInstanceId={} - id={} end",execution.getProcessInstanceId(), suId);
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        //No need ctx
        return new HashSet<>();
    }
}