package com.info803.dependency_manager_api.infrastructure.persistence.depot;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Depot {
    
    // Attributes
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = false, nullable = false, length = 100)
    private String name;

    @Column(unique = false, nullable = false, length = 200)
    private String url;

    @Column(unique = false, nullable = false, length = 200)
    private String username;

    @Column(unique = false, nullable = false, length = 200)
    private String token;

    @Column(unique = false, nullable = false)
    private Long accountId;

    @Column(unique = false, nullable = true)
    private String branch;

    @Column(unique = false, nullable = true)
    private String gitIconUrl;

    @Column(unique = false, nullable = true)
    private Date lastDependenciesUpdate;

    @Column(unique = false, nullable = true)
    private String path;

    // Constructors
    public Depot(String name, String url, String username, String token, Long accountId) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.token = token;
        this.accountId = accountId;
        this.branch = null;
        this.gitIconUrl = null;
        this.lastDependenciesUpdate = null;
    }

    @PrePersist
    private void init() {
        this.path = "depots/" + this.id;
    }

}
