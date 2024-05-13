package fr.insee.protools.backend.service.delegate.sample;

import fr.insee.protools.backend.service.context.ContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SampleServiceTask  implements JavaDelegate {

    private final ContextService contextService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String messageLog = "";
        log.info("Variables : {}", delegateExecution.getVariables());
            log.info("\t Sample service getCurrentActivityId={} - getCurrentFlowElement={} {}"
            ,delegateExecution.getCurrentActivityId(), delegateExecution.getCurrentFlowElement().getName(),messageLog);
    }
}
