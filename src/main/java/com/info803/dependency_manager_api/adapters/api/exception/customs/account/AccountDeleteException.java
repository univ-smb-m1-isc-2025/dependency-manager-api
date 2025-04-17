package com.info803.dependency_manager_api.adapters.api.exception.customs.account;

public class AccountDeleteException extends Exception {

    public AccountDeleteException(String message) {
        super(message);
    }

    public AccountDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
