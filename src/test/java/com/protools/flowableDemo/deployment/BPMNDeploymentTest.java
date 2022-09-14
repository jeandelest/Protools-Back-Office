package com.protools.flowableDemo.deployment;

import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BPMNDeploymentTest extends PluggableFlowableTestCase {

    @Test
    @Deployment(resources = {"casUsageTest.bpmn20.xml"})
    public void testDesTests(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("CasUtilisationPOC", "businessKey123");
        String businessKey = (String) runtimeService.getVariable(processInstance.getId(), "businessKeyInExecution");
        assertThat(businessKey).isEqualTo("businessKey123");

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task).isNotNull();
    }
}
