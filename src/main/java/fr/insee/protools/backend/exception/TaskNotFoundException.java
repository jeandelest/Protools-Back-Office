package fr.insee.protools.backend.exception;

import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.task.api.Task;

/**
 * This exception is thrown when you tries to access a task that does not exists
 * 
 * @author Prabhat Tripathi
 */
public class TaskNotFoundException extends FlowableObjectNotFoundException {

    /** the id of the task */
    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("No task found with id '" + taskId + "'.", Task.class);
        this.taskId=taskId;
    }

    public String getTaskId() {
        return this.taskId;
    }

}
