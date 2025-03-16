package com.info803.dependency_manager_api.adapters.api;


import com.info803.dependency_manager_api.infrastructure.persistence.Account;

import jakarta.persistence.EntityNotFoundException;

import com.info803.dependency_manager_api.application.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Retrieves a list of all accounts.
     *
     * @return a list of all Account objects stored in the repository
     */

    @GetMapping
    public List<Account> accountList() {
        logger.info("accountsList");
        return accountService.accountList();
    }

    /**
     * Retrieves a single account by its id.
     *
     * @param id the unique identifier of the account to retrieve
     * @return the Account object with the given id or null if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> account(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(accountService.account(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Creates a new account with the given email and password.
     *
     * @param account the Account object containing the email and password
     * @return a ResponseEntity indicating the success or failure of the operation
     */
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody Account account) {
        try {
            accountService.create(account.getMail(), account.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Deletes an account by its id.
     *
     * @param id the unique identifier of the account to delete
     * @return a ResponseEntity indicating the success or failure of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            accountService.delete(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
