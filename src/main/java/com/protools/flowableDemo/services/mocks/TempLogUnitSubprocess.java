package com.protools.flowableDemo.services.mocks;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TempLogUnitSubprocess implements JavaDelegate {

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Temporary Service Task Log Unit Subprocess <<  ");
        // Retrieve variables (à tester si c'est une var locale ou globale)
        Map unit = (Map) delegateExecution.getVariable("unit");
        log.info("\t \t Got unit : " + unit);
        log.info("\t \t Unit ID: " + unit.get("id"));
    }
}

