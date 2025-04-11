package com.info803.dependency_manager_api.infrastructure.persistence.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account implements UserDetails{

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = true)
    private Date verifiedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;
  
    @Column(length = 100, nullable = false)
    private String password;

    // Constructor
    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Methods
    public boolean isVerified() {
        return verifiedAt != null;
    }

    public void verify() {
        this.verifiedAt = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
