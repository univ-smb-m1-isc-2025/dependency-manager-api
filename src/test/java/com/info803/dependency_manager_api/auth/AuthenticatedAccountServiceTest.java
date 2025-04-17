package com.info803.dependency_manager_api.auth;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedAccountServiceTest {

    @InjectMocks
    private AuthenticatedAccountService authenticatedAccountService;

    @Test
    @Disabled("Authentication testing not working on CI")
    void getAuthenticatedAccount_WhenAuthenticationIsNull_ShouldReturnNull() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act
        Account result = authenticatedAccountService.getAuthenticatedAccount();

        // Assert
        assertNull(result);
    }

    @Test
    @Disabled("Authentication testing not working on CI")
    void getAuthenticatedAccount_WhenPrincipalIsNotAccount_ShouldReturnNull() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("not-an-account");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Account result = authenticatedAccountService.getAuthenticatedAccount();

        // Assert
        assertNull(result);
    }

    @Test
    @Disabled("Authentication testing not working on CI")
    void getAuthenticatedAccount_WhenPrincipalIsAccount_ShouldReturnAccount() {
        // Arrange
        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Account result = authenticatedAccountService.getAuthenticatedAccount();

        // Assert
        assertEquals(account, result);
    }
} 