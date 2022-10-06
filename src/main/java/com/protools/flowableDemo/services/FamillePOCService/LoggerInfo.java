package com.protools.flowableDemo.services.FamillePOCService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class LoggerInfo implements JavaDelegate {
    private Logger logger = LogManager.getLogger(LoggerInfo.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("send Message to send-message queue log ");
    }

}

