package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

import org.springframework.http.HttpStatus;

public class GitCommitException extends Exception {

    public static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public GitCommitException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitCommitException(String message) {
        super(message);
    }
    
}