package com.info803.dependency_manager_api.adapters.api;

public class ApiResponse<T> {
    private String message;
    private T data;

    // Constructors
    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }


}
