package fr.insee.protools.backend.service.delegate.sample;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CONTEXT;

@Component
@Slf4j
public class LogVariablesTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {

        log.info("PATH_ID={} - Variables : {}", delegateExecution.getId(),filterVariables(delegateExecution.getVariables()));
        log.info("PATH_ID={} - VariablesLocal : {}", delegateExecution.getId(),filterVariables(delegateExecution.getVariablesLocal()));
        if(delegateExecution.getParent()!=null){
            log.info("PATH_ID} - PARENT - Variables : {}", delegateExecution.getId(),filterVariables(delegateExecution.getParent().getVariables()));
            log.info("PATH_ID={} - PARENT - VariablesLocal : {}", delegateExecution.getId(),filterVariables(delegateExecution.getParent().getVariablesLocal()));
        }
    }

    Map<String,Object> filterVariables(Map<String,Object> input){
        return input.entrySet().stream()
                .filter(entry -> ! (entry.getKey().equals(VARNAME_CONTEXT)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
