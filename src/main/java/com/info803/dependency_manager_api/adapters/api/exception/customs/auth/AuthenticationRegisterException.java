package com.info803.dependency_manager_api.adapters.api.exception.customs.auth;

public class AuthenticationRegisterException extends Exception{

    public AuthenticationRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationRegisterException(String message) {
        super(message);
    }
    
}
