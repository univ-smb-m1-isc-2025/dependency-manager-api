package com.info803.dependency_manager_api.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterAccountDTO {

    @NotNull(message = "Le champ 'email' ne peut pas être null.")
    @Email(message = "Veuillez renseigner un e-mail valide.")
    private String email;

    @NotNull(message = "Le champ 'password' ne peut pas être null.")
    @Size(min = 6, message = "Votre mot de passe doit contenir au moins 6 caractères.")
    private String password;

    public RegisterAccountDTO(String username, String password) {
        this.email = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}