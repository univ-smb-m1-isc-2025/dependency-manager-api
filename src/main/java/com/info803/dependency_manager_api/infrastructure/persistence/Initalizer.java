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
            depotRepository.save(new Depot("Clement test", "https://github.com/Oziphos/test.git", "token", accountId));
        }
    }

}