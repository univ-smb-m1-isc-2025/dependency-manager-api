package com.info803.dependency_manager_api.adapters.api;


import com.info803.dependency_manager_api.infrastructure.persistence.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.application.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<Account>>> accountList() {
        logger.info("accountsList");
        try {
            List<Account> accounts = accountService.accountList();  
            return ResponseEntity.ok(new ApiResponse<>("Accounts retrieved", accounts));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Retrieves a single account by its id.
     *
     * @param id the unique identifier of the account to retrieve
     * @return the Account object with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Account>>> account(@PathVariable Long id) {
        logger.info("account");
        try {
            Optional<Account> account = accountService.account(id);  
            return ResponseEntity.ok(new ApiResponse<>("Account retrieved", account));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Creates a new account with the given account information
     *
     * @param account the Account object containing the email and password
     * @return a String indicating whether the account was created or not
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> create(@RequestBody Account account) {
        logger.info("create"); 
        try {
            accountService.create(account);  
            return ResponseEntity.ok(new ApiResponse<>("Account created"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Connects to an account with the given email and password
     * @param mail the email of the account to connect to
     * @param password the password of the account to connect to
     * @return a boolean indicating whether the account was connected or not
     */
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Boolean>> connect(@RequestBody Account account) {
        logger.info("connect"); 
        try {
            boolean connected = accountService.connect(account.getMail(), account.getPassword());  
            if (!connected) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("Password incorrect", connected, 400));
            }
            return ResponseEntity.ok(new ApiResponse<>("Connected", connected));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Deletes an account by its id.
     *
     * @param id the unique identifier of the account to delete
     * @return a String indicating whether the account was deleted or not
     */
    @GetMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        logger.info("delete"); 
        try { 
            accountService.delete(id);  
            return ResponseEntity.ok(new ApiResponse<>("Account deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Updates an account
     *
     * @param ccount the Account object containing the email and password
     * @return a String indicating whether the account was updated or not
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody Account account) {
        logger.info("update"); 
        try { 
            accountService.update(account);  
            return ResponseEntity.ok(new ApiResponse<>("Account updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Retrieves all depots owned by an account
     *
     * @param id the unique identifier of the account to retrieve depots for
     * @return a list of all Depot objects owned by the account
     */
    @GetMapping("/{id}/depots")
    public ResponseEntity<ApiResponse<List<Depot>>> accountDepots(@PathVariable Long id) {
        logger.info("accountDepots");
        try {
            List<Depot> depots = accountService.accountDepots(id);
            return ResponseEntity.ok(new ApiResponse<>("Depots retrieved", depots));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }
}
