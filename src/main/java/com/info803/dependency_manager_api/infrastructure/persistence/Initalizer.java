package com.info803.dependency_manager_api.infrastructure.persistence;

import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.application.AccountService;
import com.info803.dependency_manager_api.application.DepotService;

import jakarta.annotation.PostConstruct;

@Service
class Initializer {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final DepotRepository depotRepository;
    private final DepotService depotService;

    public Initializer(AccountRepository accountRepository, AccountService accountService, DepotRepository depotRepository, DepotService depotService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.depotRepository = depotRepository;
        this.depotService = depotService;
    }

    @PostConstruct
    public void initialize() {

        accountRepository.deleteAllInBatch();

        if (accountRepository.findAll().isEmpty()) {
            accountService.create(new Account("admin@mail.com", "admin"));
        }

        depotRepository.deleteAllInBatch();
        if (depotRepository.findAll().isEmpty()) {
            // Get the first account
            Long accountId = accountRepository.findAll().get(0).getId();
            // Github public repo
            depotService.create(new Depot("Clement test GH", "https://github.com/Oziphos/test.git", "token", accountId));
            // Gitlab 
            String GITLAB_TOKEN = System.getenv("GITLAB_TOKEN");
            depotService.create(new Depot("Clement test GL Laravel", "https://gitlab.com/Clement.Chevalier/testlaravel.git", GITLAB_TOKEN, accountId));
            // Github BattleArenaGame
            depotService.create(new Depot("BattleArenaGame", "https://github.com/Oziphos/BattleArenaGame.git", "token", accountId));
            // Github GithubCredentialsAPI
            depotService.create(new Depot("GithubCredentialsAPI", "https://github.com/Oziphos/GitHubCredentialsAPI.git", "token", accountId));
        }
    }

}