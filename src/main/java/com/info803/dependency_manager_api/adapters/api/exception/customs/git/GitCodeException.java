package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitCodeException extends Exception{

    public GitCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitCodeException(String message) {
        super(message);
    }
    
}
