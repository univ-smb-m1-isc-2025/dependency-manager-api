package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotCreationException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitBranchException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCloneException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCodeException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.git.GitScannerService;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import jakarta.ws.rs.NotFoundException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import java.io.File;
import java.text.SimpleDateFormat;

@Service
public class DepotService {

    private final DepotRepository depotRepository;
    private final AccountRepository accountRepository;
    private final GitScannerService gitScannerService;
    private final EncryptionService encryptionService;

    public DepotService(DepotRepository depotRepository, AccountRepository accountRepository, GitScannerService gitScannerService, EncryptionService encryptionService) {
        this.depotRepository = depotRepository;
        this.accountRepository = accountRepository;
        this.gitScannerService = gitScannerService;
        this.encryptionService = encryptionService;
    }

    public List<Depot> depotList() {
        return depotRepository.findAll();
    }

    public Optional<Depot> depot(Long id) throws DepotNotFoundException {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new DepotNotFoundException("Depot not found with id: " + id);
        }
        return depot;
    }

    public Depot create(Depot depot) throws AccountNotFoundException, DepotCreationException {
        Optional<Account> account = accountRepository.findById(depot.getAccountId());
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found with id: " + depot.getAccountId());
        }
        try {
            depot.setToken(encryptionService.encrypt(depot.getToken()));
    
            // Get git icon
            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());

            if (specificGit == null) {
                throw new GitNotFoundException("Git not found for url: " + depot.getUrl());
            }
            depot.setGitIconUrl(specificGit.getIconUrl());
    
            // Save the depot
            depotRepository.save(depot);
            // Clone the depot after creation
            gitClone(depot.getId());
            return depot;

        } catch (Exception e) {
            throw new DepotCreationException("Failed to create depot: ", e);
        }
    }


    public Depot update(Long id, Depot depot) throws DepotNotFoundException, DepotUpdateException {
        Optional<Depot> optExistingDepot = depotRepository.findById(id);

        if (!optExistingDepot.isPresent()) {
            throw new DepotNotFoundException("Depot not found with id: " + id);
        }

        Depot existingDepot = optExistingDepot.get();
        try {
            existingDepot.setToken(encryptionService.encrypt(depot.getToken()));
                
            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());

            if (specificGit == null) {
                throw new GitNotFoundException("Git not found for url: " + depot.getUrl());
            }
            depot.setGitIconUrl(specificGit.getIconUrl());

            depotRepository.save(existingDepot);
            return existingDepot;
        } catch (Exception e) {
            throw new DepotUpdateException("Failed to update depot: ", e);
        }
    }

    public void delete(Long id) throws DepotNotFoundException {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new DepotNotFoundException("Depot not found with id: " + id);
        }
        depotRepository.delete(depot.get());
    }

    private Depot getDepotById(Long id) throws DepotNotFoundException {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new DepotNotFoundException("Depot not found with id: " + id);
        }
        return depot.get();
    }

    public String updateLastDependenciesUpdateDate(Long id) throws DepotUpdateException {
        try {
            Depot depot = getDepotById(id);
            Date now = new Date(); // Format: "yyyy-MM-dd HH:mm:ss"
            depot.setLastDependenciesUpdate(now);
            depotRepository.save(depot);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(now);
        } catch (Exception e) {
            throw new DepotUpdateException("Failed to update last dependencies update date: ", e);
        }
    }

    public String updateDepotDependencies(Long id) throws DepotUpdateException {
        if (id == null) {
            throw new IllegalArgumentException("Depot id is null");
        }

        try {
            gitPull(id);

            gitCodeDependenciesUpdate(id);

            Thread.sleep(5000); // 5 seconds delay

            return gitPullRequest(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DepotUpdateException("Failed to update last dependencies update date: ", e);
        } catch (Exception e) {
            throw new DepotUpdateException("Failed to update last dependencies update date: ", e);
        }
    }


    // -- Depot actions --
    public String gitClone(Long id) throws GitCloneException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String msg = specificGit.executeGitAction(depot, specificGit::gitClone);
            gitGetBranch(id);
            return msg;
        } catch (Exception e) {
            throw new GitCloneException("Error cloning repository: " + e.getMessage(), e);
        }
    }

    public String gitDelete(Long id) throws GitDeleteException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitDelete);
        } catch (Exception e) {
            throw new GitDeleteException("Error deleting local repository: " + e.getMessage(), e);
        }
    }

    public String gitPull(Long id) throws GitPullException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitPull);
        } catch (Exception e) {
            throw new GitPullException("Error pulling repository: " + e.getMessage(), e);
        }
    }

    public List<File> gitCode(Long id) throws GitCodeException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCode);
        } catch (Exception e) {
            throw new GitCodeException("Error getting code: " + e.getMessage());
        }
    }

    public List<AbstractTechnology> gitCodeTechnologies(Long id) throws GitCodeException {
         try {
             Depot depot = getDepotById(id);
             AbstractGit specificGit = getSpecificGitForDepot(id);
             return specificGit.executeGitAction(depot, specificGit::gitCodeTechnologies);
         } catch (Exception e) {
             throw new GitCodeException("Error getting code technologies: " + e.getMessage(), e);
         }
    }

    public Map<String, List<Dependency>> gitCodeDependencies(Long id) throws GitCodeException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependencies);
        } catch (Exception e) {
            throw new GitCodeException("Error getting code dependencies: " + e.getMessage(), e);
        }
    }

    public String gitGetBranch(Long id) throws GitBranchException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String branch = specificGit.executeGitAction(depot, specificGit::gitGetBranch);
            depot.setBranch(branch);
            depotRepository.save(depot);
            return branch;
        } catch (Exception e) {
            throw new GitBranchException("Error getting branch: " + e.getMessage(), e);
        }
    }

    public String gitPullRequest(Long id) throws GitPullRequestException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            
            String res =  specificGit.executeGitAction(depot, specificGit::gitPullRequest);

            updateLastDependenciesUpdateDate(id);

            return res;
        } catch (RepositoryNotFoundException | GitPullRequestException e) {
            throw new GitPullRequestException("Error getting pull request: " + e.getMessage(), e);
        }
    }

    public String gitCodeDependenciesUpdate(Long id) throws GitCodeException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependenciesUpdate);
        } catch (Exception e) {
            throw new GitCodeException("Error updating code dependencies: " + e.getMessage(), e);
        }
    }

    // ----- PRIVATE METHODS -----
    private AbstractGit getSpecificGitForDepot(Long id) throws GitNotFoundException {
        try {
            Depot depot = getDepotById(id);

            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());
            if (specificGit == null) {
                throw new GitNotFoundException("Unsupported Git provider for URL: " + depot.getUrl());
            }

            return specificGit;
        } catch (Exception e) {
            throw new GitNotFoundException("Error getting specific git: " + e.getMessage(), e);
        }
    }
}
