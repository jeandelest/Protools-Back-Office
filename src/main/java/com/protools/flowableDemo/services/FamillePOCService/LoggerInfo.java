package com.protools.flowableDemo.services.FamillePOCService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.delegate.TriggerableActivityBehavior;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class loggerInfo implements JavaDelegate {
    private Logger logger = LogManager.getLogger(loggerInfo.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("send Message to send-message queue log ");
    }

}

