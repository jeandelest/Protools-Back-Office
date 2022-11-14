package com.protools.flowableDemo.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRepresentation {
    private String id;
    private String name;
    private String getParentTaskId;
    private String getProcessInstanceId;

    private String delegationState;

    public TaskRepresentation(String id, String name, String getParentTaskId, String getProcessInstanceId, String delegationState) {
        this.id = id;
        this.name = name;
        this.getParentTaskId = getParentTaskId;
        this.getProcessInstanceId = getProcessInstanceId;
        this.delegationState = delegationState;
    }


}
