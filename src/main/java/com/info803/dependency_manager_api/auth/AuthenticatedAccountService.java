package com.info803.dependency_manager_api.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;

@Service
public class AuthenticatedAccountService {

    public Account getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Account) {
            return (Account) authentication.getPrincipal();
        }
        return null;
    }
}