package com.info803.dependency_manager_api.adapters.api.exception.customs.account;

public class AccountUpdateException extends Exception {

    public AccountUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountUpdateException(String message) {
        super(message);
    }
}
