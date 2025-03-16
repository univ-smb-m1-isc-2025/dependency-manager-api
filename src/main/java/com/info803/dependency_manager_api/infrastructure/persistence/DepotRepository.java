package com.info803.dependency_manager_api.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Long> {

    // Find 
    Optional<Depot> findById(Long id);
    List<Depot> findByAccountId(Long accountId);

    // Exists
    boolean existsById(Long id);
}