package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitPushException extends Exception{
    public GitPushException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitPushException(String message) {
        super(message);
    }
}
