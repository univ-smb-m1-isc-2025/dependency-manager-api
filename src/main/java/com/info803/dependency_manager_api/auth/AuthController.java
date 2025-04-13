package com.info803.dependency_manager_api.auth;


import jakarta.validation.Valid;

import com.info803.dependency_manager_api.auth.DTO.LoginAccountDTO;
import com.info803.dependency_manager_api.auth.DTO.RegisterAccountDTO;
import com.info803.dependency_manager_api.auth.DTO.AuthResponseDTO;
import com.info803.dependency_manager_api.config.jwt.JwtService;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.adapters.api.response.ResponseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Controller
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;
    private final AuthService authenticationService;

    public AuthController(JwtService jwtService, AuthService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterAccountDTO registerAccountDTO) {
        logger.info("register");
        Account registeredAccount = authenticationService.register(registerAccountDTO);
        String jwtToken = jwtService.generateToken(registeredAccount);

        AuthResponseDTO responseDto = new AuthResponseDTO(registeredAccount, jwtToken);

        ApiResponse<AuthResponseDTO> response = ResponseUtil.success("Account registered successfully.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> authenticate(@Valid @RequestBody LoginAccountDTO loginAccountDTO) {
        logger.info("login");
        Account authenticatedAccount = authenticationService.authenticate(loginAccountDTO);
        String jwtToken = jwtService.generateToken(authenticatedAccount);

        AuthResponseDTO responseDto = new AuthResponseDTO(authenticatedAccount, jwtToken);

        ApiResponse<AuthResponseDTO> response = ResponseUtil.success("Account authenticated successfully.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        logger.info("logout");
        authenticationService.logout();
        ApiResponse<Void> response = ResponseUtil.success("Logged out successfully.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}