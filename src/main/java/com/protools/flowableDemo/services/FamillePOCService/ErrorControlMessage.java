package com.protools.flowableDemo.services.FamillePOCService;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ErrorControlMessage implements JavaDelegate {
    private Logger logger =LogManager.getLogger(ErrorControlMessage.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        logger.info("\t >> Aborting Process ... <<  ");
        // Contenu à analyser
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("\t \t >> Error Sample Size control for Survey : " + surveyName + " please check the sample size");
    }
}
