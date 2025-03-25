package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.io.File;

@Entity
public class Depot extends BddEntity{
    
    // Attributes
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String url;
    private String token;
    private Long accountId;
    private Code code;

    // Constructors
    public Depot() {}

    public Depot(String name, String url, String token, Long accountId) {
        this.name = name;
        this.url = url;
        this.token = token;
        this.accountId = accountId;
        this.code = new Code();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {    
        return token;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Code getCode() {
        return code;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setToken(String token) {    
        this.token = token;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    // Methods
    public String gitClone() {
        return code.gitClone(url, token);
    }

    public String gitPull() {
        return code.gitPull();
    }

    public File[] gitCode() {
        return code.gitCode();
    }

    public String gitDelete() {
        return code.gitDelete();
    }

    public String listDependecies() {
        return code.listDependecies();
    }
}
