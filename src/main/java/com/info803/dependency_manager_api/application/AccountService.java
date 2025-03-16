package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.DepotRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final DepotRepository depotRepository;

    public AccountService(AccountRepository accountRepository, DepotRepository depotRepository) {
        this.accountRepository = accountRepository;
        this.depotRepository = depotRepository;
    }

    public List<Account> accountList() {
        return accountRepository.findAll();
    }

    public Optional<Account> account(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }

    public void create(Account account) {
        Optional<Account> tempAccount = accountRepository.findByMail(account.getMail());
        if (tempAccount.isPresent()) {
            throw new IllegalArgumentException("Account already exists");
        }
        accountRepository.save(new Account(account.getMail(), account.getPassword()));
    }

    public void delete(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepository.delete(account.get());
    }

    public void update(Account account) {
        // Retrieve the account from the database
        Account existingAccount = accountRepository.findById(account.getId())
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Check if the email is already in use by another account
        Optional<Account> accountWithMail = accountRepository.findByMail(account.getMail());
        if (accountWithMail.isPresent() && !accountWithMail.get().getId().equals(existingAccount.getId())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Update the account in the database
        accountRepository.save(account);
    }

    public List<Depot> accountDepots(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        return depotRepository.findByAccountId(accountId);
    }
}