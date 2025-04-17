package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitPullException extends Exception {

    public GitPullException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitPullException(String message) {
        super(message);
    }

}
