package com.protools.flowableDemo.services.mocks;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleServiceTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("\t >> Sample service Task <<  ");

    }
}
