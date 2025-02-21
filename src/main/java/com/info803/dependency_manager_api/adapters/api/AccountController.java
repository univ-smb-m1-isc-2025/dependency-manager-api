package com.info803.dependency_manager_api.adapters.api;


import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.application.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @return an Optional containing the Account object corresponding to the given id, or an empty Optional if no such account exists
     */
    @GetMapping("/{id}")
    public Optional<Account> account(@PathVariable Long id) {
        logger.info("account");
        return accountService.account(id);
    }

    /**
     * Creates a new account with the given email and password.
     *
     * @param account the Account object containing the email and password
     * @return a String indicating whether the account was created or already exists
     */
    @PostMapping("/signup")
    public String signUp(@RequestBody Account account) {
        logger.info("signUp"); 
        try {
            accountService.create(account.getMail(), account.getPassword());  
            return "Account created";
        } catch (IllegalArgumentException e) {
            return "Account already exists";
        }
    }
    
    
}
