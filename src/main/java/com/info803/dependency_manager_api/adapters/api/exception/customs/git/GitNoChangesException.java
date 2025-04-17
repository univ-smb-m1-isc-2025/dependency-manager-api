package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitNoChangesException extends Exception {

    public GitNoChangesException(String message) {
        super(message);
    }

    public GitNoChangesException(String message, Throwable cause) {
        super(message, cause);
    }
}
