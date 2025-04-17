package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;


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

    public Optional<Account> me(String email) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findByEmail(email);

        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }

        return account;
    }

    public void delete(Long accountId) throws AccountDeleteException, AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }
        try {
            accountRepository.delete(account.get());
        } catch (Exception e) {
            throw new AccountDeleteException("Account not deleted : " + e.getMessage(), e);
        }
    }

    public Account update(Long accountId, Account account) throws AccountUpdateException, AccountEmailAlreadyInUseException, AccountNotFoundException {
        // Retrieve the account from the database
        Optional<Account> optExistingAccount = accountRepository.findById(accountId);
        if (!optExistingAccount.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }
        Account existingAccount = optExistingAccount.get();

        // Check if the email is already in use by another account
        Optional<Account> accountWithMail = accountRepository.findByEmail(account.getEmail());

        if (accountWithMail.isPresent() && !accountWithMail.get().getId().equals(existingAccount.getId())) {
            throw new AccountEmailAlreadyInUseException("Email already in use");
        }
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(existingAccount);
            return existingAccount;
        } catch (Exception e) {
            throw new AccountUpdateException("Account update failed: " + e.getMessage(), e);
        }
    }

    public List<Depot> accountDepots(Long accountId) throws AccountNotFoundException, DepotNotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account not found");
        }
        try {
            return depotRepository.findByAccountId(accountId);
        } catch (Exception e) {
            throw new DepotNotFoundException("Depots not found : " + e.getMessage(), e);
        }
    }
}