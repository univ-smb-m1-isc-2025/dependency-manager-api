package com.info803.dependency_manager_api.infrastructure.persistence.depot;

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

    @Column(unique = false, nullable = true, length = 200)
    private String token;

    @Column(unique = false, nullable = false)
    private Long accountId;

    @Column(unique = false, nullable = true)
    private String branch;

    // Constructors
    public Depot(String name, String url, String token, Long accountId) {
        this.name = name;
        this.url = url;
        this.token = token;
        this.accountId = accountId;
        this.branch = null;
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
