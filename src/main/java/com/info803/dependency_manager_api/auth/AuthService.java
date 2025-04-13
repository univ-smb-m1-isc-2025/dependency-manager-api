package com.info803.dependency_manager_api.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.auth.DTO.LoginAccountDTO;
import com.info803.dependency_manager_api.auth.DTO.RegisterAccountDTO;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;

@Service
public class AuthService {
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(
            AccountRepository accountRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account register(RegisterAccountDTO input) {
        if (accountRepository.existsByEmail(input.getEmail())) {
            throw new RuntimeException("E-mail already in use.");
        }

        Account account = new Account(
            input.getEmail(),
            passwordEncoder.encode(input.getPassword())
        );

        return accountRepository.save(account);
    }

    public Account authenticate(LoginAccountDTO input) {
        Account account = accountRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-mail or password invalid."));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
            );
        } catch (BadCredentialsException  e) {
            throw new BadCredentialsException("E-mail or password invalid.");
        }

        return account;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}