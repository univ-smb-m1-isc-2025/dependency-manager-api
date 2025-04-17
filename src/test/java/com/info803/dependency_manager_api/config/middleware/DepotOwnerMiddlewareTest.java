package com.info803.dependency_manager_api.config.middleware;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

@ExtendWith(MockitoExtension.class)
public class DepotOwnerMiddlewareTest {

    @Mock
    private DepotRepository depotRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private DepotOwnerMiddleware middleware;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        middleware = new DepotOwnerMiddleware(depotRepository);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void preHandle_WithNonDepotPath_ShouldReturnTrue() throws Exception {
        // Arrange
        request.setRequestURI("/other/path");

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void preHandle_WithInvalidDepotId_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/depots/invalid");
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
        request.setRequestURI("/depots/1");
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
        request.setRequestURI("/depots/1");
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
    void preHandle_WithNonExistentDepot_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/depots/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        account.setId(1L);
        when(authentication.getPrincipal()).thenReturn(account);
        when(depotRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(404, response.getStatus());
    }

    @Test
    void preHandle_WithNonOwnerDepot_ShouldReturnFalse() throws Exception {
        // Arrange
        request.setRequestURI("/depots/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        account.setId(1L);
        when(authentication.getPrincipal()).thenReturn(account);
        Depot depot = new Depot();
        depot.setId(1L);
        depot.setAccountId(2L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertFalse(result);
        assertEquals(403, response.getStatus());
    }

    @Test
    void preHandle_WithOwnerDepot_ShouldReturnTrue() throws Exception {
        // Arrange
        request.setRequestURI("/depots/1");
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", "1");
        request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", pathVariables);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        Account account = new Account();
        account.setId(1L);
        when(authentication.getPrincipal()).thenReturn(account);
        Depot depot = new Depot();
        depot.setId(1L);
        depot.setAccountId(1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));

        // Act
        boolean result = middleware.preHandle(request, response, null);

        // Assert
        assertTrue(result);
    }
} 