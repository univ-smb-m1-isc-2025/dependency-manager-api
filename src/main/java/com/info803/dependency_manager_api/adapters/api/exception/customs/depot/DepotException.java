package com.info803.dependency_manager_api.adapters.api.exception.customs.depot;

public class DepotException extends Exception{
    public DepotException(String message, Throwable cause) {
        super(message, cause);
    }

    public DepotException(String message) {
        super(message);
    }
}
