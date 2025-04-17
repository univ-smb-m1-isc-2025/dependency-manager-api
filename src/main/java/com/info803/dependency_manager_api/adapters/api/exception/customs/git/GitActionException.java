package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitActionException extends Exception {
    
    public GitActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitActionException(String message) {
        super(message);
    }
}
