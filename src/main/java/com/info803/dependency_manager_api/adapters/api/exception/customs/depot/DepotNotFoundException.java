package com.info803.dependency_manager_api.adapters.api.exception.customs.depot;

public class DepotNotFoundException extends Exception{
    public DepotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DepotNotFoundException(String message) {
        super(message);
    }
}
