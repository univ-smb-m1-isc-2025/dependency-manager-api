package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitDeleteException extends Exception {

    public GitDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitDeleteException(String message) {
        super(message);
    }
}
