package com.protools.flowableDemo.model.exceptions;


public class FileNotFoundException extends Exception {
    public FileNotFoundException(String taskId) {
        super("Please Upload a file for task : " + taskId);
    }
}
