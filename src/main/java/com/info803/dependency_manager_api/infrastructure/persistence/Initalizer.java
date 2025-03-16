package com.info803.dependency_manager_api.infrastructure.persistence;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
class Initializer {

    private final AccountRepository accountRepository;
    private final DepotRepository depotRepository;

    public Initializer(AccountRepository accountRepository, DepotRepository depotRepository) {
        this.accountRepository = accountRepository;
        this.depotRepository = depotRepository;
    }

    @PostConstruct
    public void initialize() {

        accountRepository.deleteAllInBatch();

        if (accountRepository.findAll().isEmpty()) {
            accountRepository.saveAndFlush(new Account("admin@mail", "admin"));
        }

        depotRepository.deleteAllInBatch();
        if (depotRepository.findAll().isEmpty()) {
            // Get the first account
            Long accountId = accountRepository.findAll().get(0).getId();
            depotRepository.save(new Depot("maven-central", "https://repo.maven.apache.org/maven2/", "token", accountId));
        }
    }

}