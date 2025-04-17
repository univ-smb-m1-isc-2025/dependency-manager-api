package com.info803.dependency_manager_api.adapters.api.response;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ResponseUtilTest {

    @Test
    void success_WithData_ShouldReturnSuccessResponse() {
        // Arrange
        String message = "Test message";
        String data = "Test data";

        // Act
        ApiResponse<String> response = ResponseUtil.success(message, data);

        // Assert
        assertEquals("success", response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void error_WithData_ShouldReturnErrorResponse() {
        // Arrange
        String message = "Test error message";
        String data = "Test error data";

        // Act
        ApiResponse<String> response = ResponseUtil.error(message, data);

        // Assert
        assertEquals("error", response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void error_WithoutData_ShouldReturnErrorResponse() {
        // Arrange
        String message = "Test error message";

        // Act
        ApiResponse<String> response = ResponseUtil.error(message);

        // Assert
        assertEquals("error", response.getStatus());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
    }
} 