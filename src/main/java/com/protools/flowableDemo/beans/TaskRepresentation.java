package com.protools.flowableDemo.beans;

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

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGetParentTaskId() {
        return getParentTaskId;
    }

    public void setGetParentTaskId(String getParentTaskId) {
        this.getParentTaskId = getParentTaskId;
    }

    public String getGetProcessInstanceId() {
        return getProcessInstanceId;
    }

    public void setGetProcessInstanceId(String getProcessInstanceId) {
        this.getProcessInstanceId = getProcessInstanceId;
    }

    public String getDelegationState() {
        return delegationState;
    }

    public void setDelegationState(String delegationState) {
        this.delegationState = delegationState;
    }
}
