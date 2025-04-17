package com.info803.dependency_manager_api.auth;

import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationAuthenticateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationRegisterException;
import com.info803.dependency_manager_api.auth.DTO.LoginAccountDTO;
import com.info803.dependency_manager_api.auth.DTO.RegisterAccountDTO;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterAccountDTO registerAccountDTO;
    private LoginAccountDTO loginAccountDTO;
    private Account account;

    @BeforeEach
    void setUp() {
        registerAccountDTO = new RegisterAccountDTO("test@example.com", "password");
        loginAccountDTO = new LoginAccountDTO("test@example.com", "password");

        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setPassword("encoded-password");
    }

    @Test
    void register_WhenEmailNotInUse_ShouldCreateAccount() throws AuthenticationRegisterException {
        // Arrange
        when(accountRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(accountRepository.save(any())).thenReturn(account);

        // Act
        Account result = authService.register(registerAccountDTO);

        // Assert
        assertEquals(account, result);
        verify(accountRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(accountRepository).save(any());
    }

    @Test
    void register_WhenEmailAlreadyInUse_ShouldThrowException() {
        // Arrange
        RegisterAccountDTO input = new RegisterAccountDTO("test@example.com", "password");
        when(accountRepository.existsByEmail(input.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(AuthenticationRegisterException.class, () -> authService.register(input));
    }

    @Test
    void authenticate_WhenCredentialsAreValid_ShouldReturnAccount() throws AuthenticationAuthenticateException {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("test@example.com", "password"));

        // Act
        Account result = authService.authenticate(loginAccountDTO);

        // Assert
        assertEquals(account, result);
        verify(accountRepository).findByEmail("test@example.com");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticate_WhenEmailDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationAuthenticateException.class, () -> authService.authenticate(loginAccountDTO));
        verify(accountRepository).findByEmail("test@example.com");
    }

    @Test
    void authenticate_WhenPasswordIsInvalid_ShouldThrowException() {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(AuthenticationAuthenticateException.class, () -> authService.authenticate(loginAccountDTO));
        verify(accountRepository).findByEmail("test@example.com");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void logout_ShouldClearSecurityContext() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test@example.com", "password"));

        // Act
        authService.logout();

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
} 