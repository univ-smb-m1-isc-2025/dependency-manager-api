package com.info803.dependency_manager_api.adapters.api;


import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.adapters.api.response.ResponseUtil;
import com.info803.dependency_manager_api.application.AccountService;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
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
        List<Account> accounts = accountService.accountList();  

        ApiResponse<List<Account>> response = ResponseUtil.success("Accounts retrieved", accounts);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a single account by its id.
     *
     * @param id the unique identifier of the account to retrieve
     * @return the Account object with the given id
     * @throws AccountNotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Account>>> account(@PathVariable Long id) throws AccountNotFoundException {
        logger.info("account");
        
        Optional<Account> account = accountService.account(id);  
  
        ApiResponse<Optional<Account>> response = ResponseUtil.success("Account retrieved", account);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Retrieves a single account by its email.
     *
     * @param authentication the email of the account to retrieve
     * @return the Account object with the given email
     * @throws AccountNotFoundException
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Optional<Account>>> me(final Authentication authentication) throws AccountNotFoundException {
        logger.info("me");
            
        Optional<Account> account = accountService.me(authentication.getName());  

        ApiResponse<Optional<Account>> response = ResponseUtil.success("Account retrieved", account);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Deletes an account by its id.
     *
     * @param id the unique identifier of the account to delete
     * @return a String indicating whether the account was deleted or not
     * @throws AccountDeleteException 
     * @throws AccountNotFoundException
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws AccountDeleteException, AccountNotFoundException {
        logger.info("delete"); 

        accountService.delete(id);  
    
        ApiResponse<String> response = ResponseUtil.success("Account deleted", null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an account
     *
     * @param id the unique identifier of the account to update
     * @param account the Account object containing the email and password
     * @return a String indicating whether the account was updated or not
     * @throws AccountNotFoundException
     * @throws AccountEmailAlreadyInUseException
     * @throws AccountUpdateException
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<Account>> update(@PathVariable Long id, @RequestBody Account account) throws AccountUpdateException, AccountEmailAlreadyInUseException, AccountNotFoundException {
        logger.info("update");

        Account updatedAccount = accountService.update(id, account);

        ApiResponse<Account> response = ResponseUtil.success("Account updated", updatedAccount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all depots owned by an account
     *
     * @param id the unique identifier of the account to retrieve depots for
     * @return a list of all Depot objects owned by the account
     * @throws AccountNotFoundException
     * @throws DepotNotFoundException
     */
    @GetMapping("/{id}/depots")
    public ResponseEntity<ApiResponse<List<Depot>>> accountDepots(@PathVariable Long id) throws AccountNotFoundException, DepotNotFoundException {
        logger.info("accountDepots");
        
        List<Depot> depots = accountService.accountDepots(id);

        ApiResponse<List<Depot>> response = ResponseUtil.success("Account depots retrieved", depots);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all depots owned by the current user
     *
     * @param authentication the email of the account to retrieve depots for
     * @return a list of all Depot objects owned by the account
     * @throws AccountNotFoundException
     * @throws DepotNotFoundException
     */
    @GetMapping("/me/depots")
    public ResponseEntity<ApiResponse<List<Depot>>> meDepots(final Authentication authentication) throws AccountNotFoundException, DepotNotFoundException {
        logger.info("meDepots");

        Optional<Account> account = accountService.me(authentication.getName());

        if (account.isEmpty()) {
            return new ResponseEntity<>(ResponseUtil.error("Account not found"), HttpStatus.NOT_FOUND);
        }

        List<Depot> depots = accountService.accountDepots(account.get().getId());

        ApiResponse<List<Depot>> response = ResponseUtil.success("Account depots retrieved", depots);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
