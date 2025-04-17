package com.info803.dependency_manager_api.adapters.api.exception.customs.dependency;

public class DependencyFetchingException extends Exception{

    public DependencyFetchingException(String message) {
        super(message);
    }

    public DependencyFetchingException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
