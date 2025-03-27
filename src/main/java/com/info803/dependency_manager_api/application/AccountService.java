package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.DepotRepository;
import com.info803.dependency_manager_api.infrastructure.utils.EncryptionService;

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
        try {
            account.setPassword(EncryptionService.encrypt(account.getPassword()));
            accountRepository.save(new Account(account.getMail(), account.getPassword()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Account not created : " + e.getMessage());
        }
    }

    public void delete(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        try {
            accountRepository.delete(account.get());
        } catch (Exception e) {
            throw new IllegalArgumentException("Account not deleted : " + e.getMessage());
        }
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
        try {
            account.setPassword(EncryptionService.encrypt(account.getPassword()));
            existingAccount.updateFrom(account);
            accountRepository.save(existingAccount);
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

    public boolean connect(String mail, String password) {
        Optional<Account> account = accountRepository.findByMail(mail);
        // Check if the account exists
        if (!account.isPresent()) {
            throw new IllegalArgumentException("Account not found with mail : " + mail);
        }
        // Check if the account is verified
        if (account.get().getVerifiedAt() == null) {
            throw new IllegalArgumentException("Account not verified");
        }
        // Check if the password is correct
        try {
            return EncryptionService.verifyHash(password, account.get().getPassword());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while comparing password : " + e.getMessage());
        }
    }
}