package com.info803.dependency_manager_api.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Find 
    Optional<Account> findById(Long id);
    Optional <Account> findByMail(String mail);

    // Exists
    boolean existsById(Long id);
    boolean existsByMail(String mail);
}