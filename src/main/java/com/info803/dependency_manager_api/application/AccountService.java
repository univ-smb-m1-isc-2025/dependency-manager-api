package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public List<Account> accountList() {
        return repository.findAll();
    }

    public Optional<Account> account(Long accountId) {
        Optional<Account> account = repository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }
    
    public void delete(Long accountId) {
        Optional<Account> account = repository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        repository.delete(account.get());
    }

    public void create(String mail, String password) {
        Optional<Account> account = repository.findByMail(mail);
        if (account.isPresent()) {
            throw new IllegalArgumentException("Account already exists");
        }
        repository.save(new Account(mail, password));
    }

    public void update(Account account) {
        // Retrieve the account from the database
        Account existingAccount = repository.findById(account.getId())
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Check if the email is already in use by another account
        Optional<Account> accountWithMail = repository.findByMail(account.getMail());
        if (accountWithMail.isPresent() && !accountWithMail.get().getId().equals(existingAccount.getId())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Update the account in the database
        repository.save(account);
    }
}