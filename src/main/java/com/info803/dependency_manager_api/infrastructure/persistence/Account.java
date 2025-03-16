package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String mail;

    private String createdAt;
    private String mailVerifiedAt;
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
        this.mailVerifiedAt = null;
    }

    // Getters
    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMailVerifiedAt() {
        return mailVerifiedAt;
    }

    public Long getId() {
        return id;
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

    public void setMailVerifiedAt(String mailVerifiedAt) {
        this.mailVerifiedAt = mailVerifiedAt;
    }
}
