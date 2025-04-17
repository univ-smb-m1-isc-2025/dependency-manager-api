package com.info803.dependency_manager_api.adapters.api.exception.customs.dependency;

public class DependencyDetectLatestVersionException extends Exception {
    public DependencyDetectLatestVersionException(String message) {
        super(message);
    }

    public DependencyDetectLatestVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}