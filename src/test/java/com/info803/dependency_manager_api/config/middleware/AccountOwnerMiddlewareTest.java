package com.info803.dependency_manager_api.config.middleware;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;

@ExtendWith(MockitoExtension.class)
public class AccountOwnerMiddlewareTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private AccountOwnerMiddleware middleware;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        middleware = new AccountOwnerMiddleware();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void preHandle_WithNonAccountPath_ShouldReturnTrue() throws Exception {
        // Arrange
        request.setRequestURI("/other/path");

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void preHandle_WithInvalidAccountId_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/invalid");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "invalid");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(400, response.getStatus());
    }

    @Test
    void preHandle_WithUnauthenticatedUser_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandle_WithNonAccountPrincipal_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandle_WithNullAccountId_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        when(authentication.getPrincipal()).thenReturn(account);

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(500, response.getStatus());
    }

    @Test
    void preHandle_WithNonOwnerAccount_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/2");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "2");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        account.setId(1L);
        when(authentication.getPrincipal()).thenReturn(account);

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(403, response.getStatus());
    }

    @Test
    void preHandle_WithOwnerAccount_ShouldReturnTrue() throws Exception {
        // Arrange
        request.setRequestURI("/accounts/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        account.setId(1L);
        when(authentication.getPrincipal()).thenReturn(account);

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertTrue(result);
    }
} 