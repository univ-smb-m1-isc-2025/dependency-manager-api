package com.info803.dependency_manager_api.adapters.api.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationAuthenticateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCheckoutException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleAccountNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        String message = "Account not found";
        AccountNotFoundException ex = new AccountNotFoundException(message);

        // Act
        ResponseEntity<ApiResponse<String>> response = handler.handleAccountNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handleAuthenticationAuthenticatedException_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String message = "Authentication failed";
        AuthenticationAuthenticateException ex = new AuthenticationAuthenticateException(message);

        // Act
        ResponseEntity<ApiResponse<String>> response = handler.handleAuthenticationAuthenticatedException(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handleDependencyDetectLatestVersionException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        String message = "Failed to detect latest version";
        DependencyDetectLatestVersionException ex = new DependencyDetectLatestVersionException(message);

        // Act
        ResponseEntity<ApiResponse<String>> response = handler.handleDependencyDetectLatestVersionException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handleDepotNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        String message = "Depot not found";
        DepotNotFoundException ex = new DepotNotFoundException(message);

        // Act
        ResponseEntity<ApiResponse<String>> response = handler.handleDepotNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handleGitCheckoutException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        String message = "Git checkout failed";
        GitCheckoutException ex = new GitCheckoutException(message);

        // Act
        ResponseEntity<ApiResponse<String>> response = handler.handleGitCheckoutException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
    }
} 