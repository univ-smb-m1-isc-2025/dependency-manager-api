package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Account extends BddEntity {
    @Column(unique = true)
    private String mail;

    private String createdAt;
    private String verifiedAt;
    @JsonIgnore
    private String password;

    // Constructor
    public Account() {}

    public Account(String mail, String password) {
        this.mail = mail;
        this.password = password;
        // Date creation
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.createdAt = dateFormat.format(date);
        this.verifiedAt = null;
    }

    // Getters
    public String getMail() {
        return mail;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }

    // Setters
    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setVerifiedAt(String verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
