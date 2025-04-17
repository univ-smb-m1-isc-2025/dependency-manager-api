package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitCloneException extends Exception {

    public GitCloneException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitCloneException(String message) {
        super(message);
    }
}
