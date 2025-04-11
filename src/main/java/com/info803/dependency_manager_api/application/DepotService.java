package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.git.GitDetector;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.File;

@Service
public class DepotService {
    private final DepotRepository depotRepository;
    private final AccountRepository accountRepository;
    private final GitDetector gitDetector;

    public DepotService(DepotRepository depotRepository, AccountRepository accountRepository) {
        this.depotRepository = depotRepository;
        this.accountRepository = accountRepository;
        this.gitDetector = new GitDetector();
    }

    public List<Depot> depotList() {
        return depotRepository.findAll();
    }

    public Optional<Depot> depot(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        return depot;
    }

    public Depot create(Depot depot) {
        Optional<Account> account = accountRepository.findById(depot.getAccountId());
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        depotRepository.save(depot);
        return depot;
    }

    public void delete(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        depotRepository.deleteById(id);
    }

    public Depot update(Depot depot) {
        Depot existingDepot = depotRepository.findById(depot.getId()).orElseThrow(() -> new IllegalArgumentException("Depot not found"));

        depotRepository.save(existingDepot);
        return existingDepot;
    }

    // -- Depot actions --
    public String gitClone(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            
            return git.gitClone(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitPull(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            return git.gitPull(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<File> gitCode(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            return git.gitCode(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
    }

    public String gitDelete(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try { 
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            return git.gitDelete(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<AbstractTechnology> gitCodeTechnologies(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            return git.gitCodeTechnologies(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Map<String, List<Dependency>> gitCodeDependencies(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            return git.gitCodeDependencies(depot);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitGetBranch(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            Depot depot = optionalDepot.get();
            AbstractGit git = gitDetector.detectGit(depot.getUrl());
            String branch = git.gitGetBranch(depot);
            depot.setBranch(branch);
            depotRepository.save(depot);
            return branch;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
