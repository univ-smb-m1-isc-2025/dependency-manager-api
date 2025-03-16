package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.DepotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepotService {
    private final DepotRepository repository;

    public DepotService(DepotRepository repository) {
        this.repository = repository;
    }

    public List<Depot> depotList() {
        return repository.findAll();
    }

    public Optional<Depot> depot(Long id) {
        Optional<Depot> depot = repository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        return depot;
    }

    public void create(Depot depot) {
        repository.save(depot);
    }

    public void delete(Long id) {
        Optional<Depot> depot = repository.findById(id);
        if (!depot.isPresent()) {
            throw new IllegalArgumentException("Depot not found");
        }
        repository.deleteById(id);
    }

    public void update(Depot depot) {
        // TODO : implement
    }
    
}
