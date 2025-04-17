package com.info803.dependency_manager_api.adapters.api.exception.customs.account;

public class AccountEmailAlreadyInUseException extends Exception{

    public AccountEmailAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountEmailAlreadyInUseException(String message) {
        super(message);
    }
}
