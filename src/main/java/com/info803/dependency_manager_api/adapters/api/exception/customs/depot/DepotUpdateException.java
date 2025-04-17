package com.info803.dependency_manager_api.adapters.api.exception.customs.depot;

public class DepotUpdateException extends Exception {
    public DepotUpdateException(String message, Throwable cause) {    
        super(message, cause);
    }

    public DepotUpdateException(String message) {
        super(message);
    }
}
