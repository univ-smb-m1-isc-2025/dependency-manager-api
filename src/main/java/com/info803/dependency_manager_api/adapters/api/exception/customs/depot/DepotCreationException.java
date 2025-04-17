package com.info803.dependency_manager_api.adapters.api.exception.customs.depot;

public class DepotCreationException extends Exception{
    public DepotCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DepotCreationException(String message) {
        super(message);
    }
}
