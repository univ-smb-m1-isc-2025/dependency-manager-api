package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitBranchException extends Exception{

    public GitBranchException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitBranchException(String message) {
        super(message);
    }
    
}
