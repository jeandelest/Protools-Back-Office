package fr.insee.protools.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.platine.pilotage.dto.PeriodEnum;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * All the delegate referenced in BPMN should implement this interface so we can make BPMN introspection to validate
 * that the provided context contains all the required information for every task of the BPMN
 */
public interface DelegateContextVerifier {

    Set<String> getContextErrors(JsonNode contextRootNode);

    default String computeMissingMessage(String missingElement, Class<?> classUsingThisElement){
        return String.format("Class=%s : Missing Context element name=%s ", classUsingThisElement.getSimpleName(),missingElement);
    }

    default String computeIncorrectMessage(String incorrectElement, String message,Class<?> classUsingThisElement){
        return String.format("Class=%s : Wrong Context element name=%s - message=%s ", classUsingThisElement.getSimpleName(),incorrectElement,message);
    }
    default String computeIncorrectEnumMessage(String incorrectElement,String value, String enumValues, Class<?> classUsingThisElement){
        return String.format("Class=%s : Incorrect enum name=%s - value=[%s] - expected one of %s"
                ,classUsingThisElement.getSimpleName()
                ,incorrectElement
                , value
                ,Arrays.toString(PeriodEnum.values()));
    }
    default Set<String> computeMissingChildrenMessages(Set<String> requiredChildren, JsonNode parentNode, Class<?> classUsingThisElement){
        Set<String> missingNodes = new HashSet<>();
        for (String child: requiredChildren  ) {
            if(parentNode.get(child) == null){
                missingNodes.add(computeMissingMessage(child,classUsingThisElement));
            }
        }
        return missingNodes;
    }

    default void checkContextOrThrow(Logger log,String processInstanceId, JsonNode contextRootNode) {
        if(contextRootNode==null)
            throw new BadContextIncorrectException(String.format("ProcessInstanceId=%s - context is missing", processInstanceId));

        var errors = getContextErrors(contextRootNode);
        if(!errors.isEmpty()){
            for (var msg: errors) {
                log.error(msg);
            }
            throw new BadContextIncorrectException(String.format("ProcessInstanceId=%s - context is incorrect missingNodes=%s", processInstanceId,errors));
        }
    }
}


