package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitNotFoundException extends Exception {
    public GitNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitNotFoundException(String message) {
        super(message);
    }
}
