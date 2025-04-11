package com.info803.dependency_manager_api.infrastructure.persistence;

import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.auth.AuthService;
import com.info803.dependency_manager_api.application.DepotService;
import com.info803.dependency_manager_api.auth.DTO.RegisterAccountDTO;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import jakarta.annotation.PostConstruct;

@Service
class Initializer {

    private final AccountRepository accountRepository;
    private final AuthService authenticationService;
    private final DepotRepository depotRepository;
    private final DepotService depotService;

    public Initializer(AccountRepository accountRepository, AuthService authenticationService, DepotRepository depotRepository, DepotService depotService) {
        this.accountRepository = accountRepository;
        this.authenticationService = authenticationService;
        this.depotRepository = depotRepository;
        this.depotService = depotService;
    }

    @PostConstruct
    public void initialize() {

        accountRepository.deleteAllInBatch();

        if (accountRepository.findAll().isEmpty()) {
            authenticationService.register(new RegisterAccountDTO("admin@mail.com", "admin"));
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
            // Github this project
            depotService.create(new Depot("DependencyManagerAPI", "https://github.com/univ-smb-m1-isc-2025/dependency-manager-api.git", "token", accountId));
            // Github Test Project
            depotService.create(new Depot("TestProject", "https://github.com/Oziphos/test.git", "token", accountId));
        }
    }

}