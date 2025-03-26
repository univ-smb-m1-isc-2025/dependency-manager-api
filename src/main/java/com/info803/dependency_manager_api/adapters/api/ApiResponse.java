package com.info803.dependency_manager_api.adapters.api;

public class ApiResponse<T> {
    private int status = 200;
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

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, T data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
