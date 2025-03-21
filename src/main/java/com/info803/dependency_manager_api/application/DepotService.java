package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.DepotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.io.File;

@Service
public class DepotService {
    private final DepotRepository depotRepository;
    private final AccountRepository accountRepository;

    public DepotService(DepotRepository depotRepository, AccountRepository accountRepository) {
        this.depotRepository = depotRepository;
        this.accountRepository = accountRepository;
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

    public void create(Depot depot) {
        Optional<Account> account = accountRepository.findById(depot.getAccountId());
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        depotRepository.save(depot);
    }

    public void delete(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        depotRepository.deleteById(id);
    }

    public void update(Depot depot) {
        Depot existingDepot = depotRepository.findById(depot.getId()).orElseThrow(() -> new IllegalArgumentException("Depot not found"));
        
        existingDepot.updateFrom(depot);
        depotRepository.save(existingDepot);
    }

    public String gitClone(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        return depot.get().gitClone();
    }

    public String gitPull(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        return depot.get().gitPull();
    }

    public File[] gitCode(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try {
            return depot.get().gitCode();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
    }

    public String gitDelete(Long id) {
        Optional<Depot> depot = depotRepository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        try { 
            return depot.get().gitDelete();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
