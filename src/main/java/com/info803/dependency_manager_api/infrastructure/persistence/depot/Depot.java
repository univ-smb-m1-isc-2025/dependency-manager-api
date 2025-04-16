package com.info803.dependency_manager_api.infrastructure.persistence.depot;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

    // Methods

    /**
     * Returns the path of the depot
     * @return the path of the depot
     */
    public String getPath() {
        return "depots/" + this.id;
    }
}
