package com.info803.dependency_manager_api.auth;

import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationAuthenticateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationRegisterException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.auth.DTO.AuthResponseDTO;
import com.info803.dependency_manager_api.auth.DTO.LoginAccountDTO;
import com.info803.dependency_manager_api.auth.DTO.RegisterAccountDTO;
import com.info803.dependency_manager_api.config.jwt.JwtService;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterAccountDTO registerAccountDTO;
    private LoginAccountDTO loginAccountDTO;
    private Account account;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        registerAccountDTO = new RegisterAccountDTO("test@example.com", "password");
        loginAccountDTO = new LoginAccountDTO("test@example.com", "password");

        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setPassword("encoded-password");

        jwtToken = "test-jwt-token";
    }

    @Test
    void register_ShouldReturnCreatedResponse() throws AuthenticationRegisterException {
        // Arrange
        when(authService.register(any())).thenReturn(account);
        when(jwtService.generateToken(any())).thenReturn(jwtToken);

        // Act
        ResponseEntity<ApiResponse<AuthResponseDTO>> response = authController.register(registerAccountDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account registered successfully.", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(account, response.getBody().getData().getAccount());
        assertEquals(jwtToken, response.getBody().getData().getToken());

        verify(authService).register(registerAccountDTO);
        verify(jwtService).generateToken(account);
    }

    @Test
    void authenticate_ShouldReturnOkResponse() throws AuthenticationAuthenticateException {
        // Arrange
        when(authService.authenticate(any())).thenReturn(account);
        when(jwtService.generateToken(any())).thenReturn(jwtToken);

        // Act
        ResponseEntity<ApiResponse<AuthResponseDTO>> response = authController.authenticate(loginAccountDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account authenticated successfully.", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(account, response.getBody().getData().getAccount());
        assertEquals(jwtToken, response.getBody().getData().getToken());

        verify(authService).authenticate(loginAccountDTO);
        verify(jwtService).generateToken(account);
    }

    @Test
    void logout_ShouldReturnOkResponse() {
        // Act
        ResponseEntity<ApiResponse<Void>> response = authController.logout();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Logged out successfully.", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(authService).logout();
    }
} 