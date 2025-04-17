package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.git.GitScannerService;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public Optional<Depot> depot(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found with id: " + id);
        }
        return depot;
    }

    public Depot create(Depot depot) throws AccountNotFoundException, DepotCreationException, GitNotFoundException, GitBranchException, GitCloneException {
        Optional<Account> account = accountRepository.findById(depot.getAccountId());
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found with id: " + depot.getAccountId());
        }
        try {
            depot.setToken(encryptionService.encrypt(depot.getToken()));
    
            // Get git icon
            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());

            if (specificGit == null) {
                throw new IllegalArgumentException("Git not found for url: " + depot.getUrl());
            }
            depot.setGitIconUrl(specificGit.getIconUrl());
    
            // Save the depot
            Depot savedDepot = depotRepository.save(depot);
            // Clone the depot after creation using the same specificGit instance
            gitClone(savedDepot.getId());
            return savedDepot;
        } catch(GitBranchException e) {
            throw e;
        } catch (GitCloneException e) {
            throw e;
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Depot update(Long id, Depot depot) throws DepotNotFoundException, DepotUpdateException, GitNotFoundException {
        Optional<Depot> optExistingDepot = depotRepository.findById(id);

        if (!optExistingDepot.isPresent()) {
            throw new DepotNotFoundException("Depot not found with id: " + id);
        }

        Depot existingDepot = optExistingDepot.get();
        try {
            existingDepot.setToken(encryptionService.encrypt(depot.getToken()));
                
            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());

            if (specificGit == null) {
                throw new IllegalArgumentException("Git not found for url: " + depot.getUrl());
            }
            depot.setGitIconUrl(specificGit.getIconUrl());

            depotRepository.save(existingDepot);
            return existingDepot;
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found with id: " + id);
        }
        depotRepository.delete(depot.get());
    }

    private Depot getDepotById(Long id) {
         return depotRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Depot not found with id: " + id));
    }

    public String updateLastDependenciesUpdateDate(Long id) throws DepotUpdateException, DepotNotFoundException {
        try {
            Depot depot = getDepotById(id);
            Date now = new Date(); // Format: "yyyy-MM-dd HH:mm:ss"
            depot.setLastDependenciesUpdate(now);
            depotRepository.save(depot);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(now);
        } catch (DepotNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String updateDepotDependencies(Long id) throws DepotUpdateException, DepotNotFoundException, GitPullException, GitCodeException {
        if (id == null) {
            throw new IllegalArgumentException("Depot id is null");
        }

        try {
            gitPull(id);

            gitCodeDependenciesUpdate(id);

            Thread.sleep(5000);

            return gitPullRequest(id);
        } catch (GitPullException e) {
            throw e;
        } catch (GitCodeException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DepotUpdateException("Failed to update last dependencies update date: ", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    // -- Depot actions --
    public String gitClone(Long id) throws GitCloneException, GitNotFoundException, GitBranchException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String msg = specificGit.executeGitAction(depot, specificGit::gitClone);
            gitGetBranch(id);
            return msg;
        } catch (GitNotFoundException e) {
            throw e;
        } catch (GitBranchException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitDelete(Long id) throws GitDeleteException, GitNotFoundException {
        try {
            Depot depot = optionalDepot.get();
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitDelete);
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting local repository: " + e.getMessage());
        }
    }

    public String gitPull(Long id) throws GitPullException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitPull);
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<File> gitCode(Long id) throws GitCodeException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            // gitCode doesn't need credentials usually, but executeGitAction handles decryption if needed.
            return specificGit.executeGitAction(depot, specificGit::gitCode);
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<AbstractTechnology> gitCodeTechnologies(Long id) throws GitCodeException, GitNotFoundException {
         try {
             Depot depot = getDepotById(id);
             AbstractGit specificGit = getSpecificGitForDepot(id);
             return specificGit.executeGitAction(depot, specificGit::gitCodeTechnologies);
         } catch (GitNotFoundException e) {
             throw e;
         } catch (Exception e) {
             throw new IllegalArgumentException(e.getMessage());
         }
    }

    public Map<String, List<Dependency>> gitCodeDependencies(Long id) throws GitCodeException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependencies);
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitGetBranch(Long id) throws GitBranchException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String branch = specificGit.executeGitAction(depot, specificGit::gitGetBranch);
            depot.setBranch(branch); // Assuming executeGitAction decrypts/re-encrypts token if needed
            depotRepository.save(depot);
            return branch;
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitPullRequest(Long id) throws GitPullRequestException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            
            String res =  specificGit.executeGitAction(depot, specificGit::gitPullRequest);

            updateLastDependenciesUpdateDate(id);

            return res;
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitPullRequestException("Error getting pull request: " + e.getMessage(), e);
        }
    }

    public String gitCodeDependenciesUpdate(Long id) throws GitCodeException, GitNotFoundException {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependenciesUpdate);
        } catch (GitNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
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
