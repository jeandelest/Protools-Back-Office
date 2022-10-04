package com.protools.flowableDemo.services.FamillePOCService;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class sendGlobalErrorMessage implements JavaDelegate {
    private Logger logger =LogManager.getLogger(com.protools.flowableDemo.services.FamillePOCService.sendGlobalErrorMessage.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        // Contenu à analyser
        String surveyName = (String) delegateExecution.getVariable("name");
        logger.info("\t \t >> The Sample : " + surveyName + "Has encountered an error, please contact technical support");
    }
}
