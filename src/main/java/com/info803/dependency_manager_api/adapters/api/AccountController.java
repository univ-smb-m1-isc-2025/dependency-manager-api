package com.info803.dependency_manager_api.adapters.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.info803.dependency_manager_api.application.AccountService;
@RestController
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "/api/accounts")
    public String accountList() {
        logger.info("accountsList");
        return "accountsList";
        //var accounts = accountService.accountList();
        //return accounts != null ? accounts.toString() : "No accounts found";
    }
    
}
