package com.protools.flowableDemo.services.FamillePOCService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.delegate.TriggerableActivityBehavior;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class testTriggerTask implements JavaDelegate, TriggerableActivityBehavior, Serializable {
    private Logger logger = LogManager.getLogger(testTriggerTask.class);

    @Override
    public void trigger(DelegateExecution delegateExecution, String signalName, Object signalData) {
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("Triggered service task with signal " + signalName + " and data " + signalData+ "for survey : " + surveyName);
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("TestTriggerTask execution part, for survey: "+surveyName);
    }

}

