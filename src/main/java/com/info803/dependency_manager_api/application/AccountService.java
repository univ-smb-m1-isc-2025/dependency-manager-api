package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.AccountRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public List<Account> accountList() {
        return repository.findAll();
    }

    public Account account(Long accountId) {
        return repository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
    
    public void delete(Long accountId) {
        if (!repository.existsById(accountId)) {
            throw new EntityNotFoundException("Account not found");
        }
        repository.deleteById(accountId);
    }

    public void create(String mail, String password) {
        if (repository.findByMail(mail).isPresent()) {
            throw new IllegalStateException("Account already exists");
        }
        repository.save(new Account(mail, password));
    }
}