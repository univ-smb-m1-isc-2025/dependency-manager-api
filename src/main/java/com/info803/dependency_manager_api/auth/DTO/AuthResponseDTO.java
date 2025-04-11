package com.info803.dependency_manager_api.auth.DTO;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;

public class AuthResponseDTO {
        private String email;
        private String token;

    public AuthResponseDTO(Account account, String token) {
        this.email = account.getEmail();
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}