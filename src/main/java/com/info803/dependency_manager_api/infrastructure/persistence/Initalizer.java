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
            // Github public repo
            depotRepository.save(new Depot("Clement test GH", "https://github.com/Oziphos/test.git", "token", accountId));
            // Gitlab 
            String GITLAB_TOKEN = System.getenv("GITLAB_TOKEN");
            depotRepository.save(new Depot("Clement test GL Laravel", "https://gitlab.com/Clement.Chevalier/testlaravel.git", GITLAB_TOKEN, accountId));
            // Github BattleArenaGame
            depotRepository.save(new Depot("BattleArenaGame", "https://github.com/Oziphos/BattleArenaGame.git", "token", accountId));
            // Github GithubCredentialsAPI
            depotRepository.save(new Depot("GithubCredentialsAPI", "https://github.com/Oziphos/GitHubCredentialsAPI.git", "token", accountId));
        }
    }

}