package com.protools.flowableDemo.services.messhugah;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMailRelanceServiceTask implements JavaDelegate {

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info(">> SendMailRelanceServiceTask");
    }
}
