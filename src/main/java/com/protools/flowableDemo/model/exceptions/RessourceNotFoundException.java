package com.protools.flowableDemo.model.exceptions;

public class RessourceNotFoundException extends RuntimeException{
    public RessourceNotFoundException(String ressource, String id) {
        super(String.format("No '%s' for value : '%s'", ressource, id));
    }
}
