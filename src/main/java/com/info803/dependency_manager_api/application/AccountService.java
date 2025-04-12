package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;


@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final DepotRepository depotRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, DepotRepository depotRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.depotRepository = depotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Account> accountList() {
        return accountRepository.findAll();
    }

    public Optional<Account> account(Long accountId) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }
        return account;
    }

    public Optional<Account> me(String email) {
        return accountRepository.findByEmail(email);
    }

    public void delete(Long accountId) throws AccountException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }
        try {
            accountRepository.delete(account.get());
        } catch (Exception e) {
            throw new AccountException("Account not deleted : " + e.getMessage());
        }
    }

    public Account update(Long accountId, Account account) {
        // Retrieve the account from the database
        Account existingAccount = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Check if the email is already in use by another account
        Optional<Account> accountWithMail = accountRepository.findByEmail(account.getEmail());
        if (accountWithMail.isPresent() && !accountWithMail.get().getId().equals(existingAccount.getId())) {
            throw new IllegalArgumentException("Email already in use");
        }
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(existingAccount);
            return existingAccount;
        } catch (Exception e) {
            throw new IllegalArgumentException("Account not updated : " + e.getMessage());
        }
    }

    public List<Depot> accountDepots(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        try {
            return depotRepository.findByAccountId(accountId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Depots not found : " + e.getMessage());
        }
    }
}