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

    public Depot create(Depot depot) {
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
            depotRepository.save(depot);
            // Clone the depot after creation
            gitClone(depot.getId());
            return depot;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Depot update(Long id, Depot depot) {
        Depot existingDepot = depotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Depot not found with id: " + id));
        try {
            existingDepot.setToken(encryptionService.encrypt(depot.getToken()));
                
            AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());

            if (specificGit == null) {
                throw new IllegalArgumentException("Git not found for url: " + depot.getUrl());
            }
            depot.setGitIconUrl(specificGit.getIconUrl());

            depotRepository.save(existingDepot);
            return existingDepot;
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

    public String updateLastDependenciesUpdateDate(Long id) {
        try {
            Depot depot = getDepotById(id);
            Date now = new Date(); // Format: "yyyy-MM-dd HH:mm:ss"
            depot.setLastDependenciesUpdate(now);
            depotRepository.save(depot);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(now);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String updateDepotDependencies(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Depot id is null");
        }

        try {
            gitPull(id);

            gitCodeDependenciesUpdate(id);

            Thread.sleep(5000);

            return gitPullRequest(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    // -- Depot actions --
    public String gitClone(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String msg = specificGit.executeGitAction(depot, specificGit::gitClone);
            gitGetBranch(id);
            return msg;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitDelete(Long id) {
        Optional<Depot> optionalDepot = depotRepository.findById(id);
        if (!optionalDepot.isPresent()) {
            throw new IllegalArgumentException("Depot not found with id: " + id);
        }
        // Attempt to delete the local clone if it exists
        try {
            Depot depot = optionalDepot.get();
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitDelete);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting local repository: " + e.getMessage());
        }
    }

    public String gitPull(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitPull);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<File> gitCode(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            // gitCode doesn't need credentials usually, but executeGitAction handles decryption if needed.
            return specificGit.executeGitAction(depot, specificGit::gitCode);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<AbstractTechnology> gitCodeTechnologies(Long id) {
         try {
             Depot depot = getDepotById(id);
             AbstractGit specificGit = getSpecificGitForDepot(id);
             return specificGit.executeGitAction(depot, specificGit::gitCodeTechnologies);
         } catch (Exception e) {
             throw new IllegalArgumentException(e.getMessage());
         }
    }

    public Map<String, List<Dependency>> gitCodeDependencies(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependencies);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitGetBranch(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            String branch = specificGit.executeGitAction(depot, specificGit::gitGetBranch);
            depot.setBranch(branch); // Assuming executeGitAction decrypts/re-encrypts token if needed
            depotRepository.save(depot);
            return branch;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitPullRequest(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            
            String res =  specificGit.executeGitAction(depot, specificGit::gitPullRequest);

            updateLastDependenciesUpdateDate(id);

            return res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String gitCodeDependenciesUpdate(Long id) {
        try {
            Depot depot = getDepotById(id);
            AbstractGit specificGit = getSpecificGitForDepot(id);
            return specificGit.executeGitAction(depot, specificGit::gitCodeDependenciesUpdate);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // ----- PRIVATE METHODS -----
    private AbstractGit getSpecificGitForDepot(Long id) {
        Depot depot = depotRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Depot not found with id: " + id));
        AbstractGit specificGit = gitScannerService.detectGit(depot.getUrl());
        if (specificGit == null) {
            throw new IllegalArgumentException("Unsupported Git provider for URL: " + depot.getUrl());
        }
        return specificGit;
    }
}
