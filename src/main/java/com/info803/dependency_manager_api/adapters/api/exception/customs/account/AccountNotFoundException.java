package com.info803.dependency_manager_api.adapters.api.exception.customs.account;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccountNotFoundException(String message) {
        super(message);
    }
    
}
