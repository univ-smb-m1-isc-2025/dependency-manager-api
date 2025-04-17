package com.info803.dependency_manager_api.adapters.api.exception.customs.git;

public class GitAddException extends Exception{

    public GitAddException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitAddException(String message) {
        super(message);
    }
}
