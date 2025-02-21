package com.info803.dependency_manager_api.infrastructure.persistence;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
class Initializer {

    private final AccountRepository repository;

    public Initializer(AccountRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void initialize() {

        repository.deleteAllInBatch();

        if (repository.findAll().isEmpty()) {
            repository.saveAndFlush(new Account("admin@mail", "admin"));
        }
    }

}