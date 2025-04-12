package com.info803.dependency_manager_api.infrastructure.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    @Value("${encryption.githubToken}")
    private Resource githubTokenFile;

    @Value("${encryption.gitlabToken}")
    private Resource gitlabTokenFile;

    public Initializer(AccountRepository accountRepository, AuthService authenticationService, DepotRepository depotRepository, DepotService depotService) {
        this.accountRepository = accountRepository;
        this.authenticationService = authenticationService;
        this.depotRepository = depotRepository;
        this.depotService = depotService;
    }

    @PostConstruct
    public void initialize() {

        String githubToken = "";
        String gitlabToken = "";
        try {
            githubToken = new String(githubTokenFile.getInputStream().readAllBytes());
            gitlabToken = new String(gitlabTokenFile.getInputStream().readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        accountRepository.deleteAllInBatch();

        if (accountRepository.findAll().isEmpty()) {
            authenticationService.register(new RegisterAccountDTO("admin@mail.com", "admin"));
        }

        depotRepository.deleteAllInBatch();
        if (depotRepository.findAll().isEmpty()) {
            // Get the first account
            Long accountId = accountRepository.findAll().get(0).getId();
            // Github 
            depotService.create(new Depot("Clement test GH", "https://github.com/Oziphos/test.git", "Oziphos", githubToken, accountId));
            // Github
            depotService.create(new Depot("DependencyManagerAPI", "https://github.com/univ-smb-m1-isc-2025/dependency-manager-api.git", "univ-smb-m1-isc-2025", githubToken, accountId));
            // Gitlab
            depotService.create(new Depot("Clement SpringTest GL", "https://gitlab.com/Clement.Chevalier/springtest.git", "Clement.Chevalier", gitlabToken, accountId));
        }
    }

}