package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitPullRequestException extends Exception {

    public GitPullRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitPullRequestException(String message) {
        super(message);
    }
}
