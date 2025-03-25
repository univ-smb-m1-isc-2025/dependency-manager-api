package com.info803.dependency_manager_api.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    Optional<Code> findById(Long id);
    Optional<Code> findByDepotId(Long id);

}
