package com.info803.dependency_manager_api.adapters.api.exception.customs.git;
public class GitCheckoutException extends Exception {


    public GitCheckoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitCheckoutException(String message) {
        super(message);
    }
    
}
