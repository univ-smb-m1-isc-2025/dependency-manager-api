package com.info803.dependency_manager_api.auth.DTO;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;

public class AuthResponseDTO {
        private Account account;
        private String token;

    public AuthResponseDTO(Account account, String token) {
        this.account = account;
        this.token = token;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}