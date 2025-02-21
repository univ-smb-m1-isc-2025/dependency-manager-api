package com.info803.dependency_manager_api.adapters.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.info803.dependency_manager_api.application.AccountService;

@RestController
public class SignUpController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AccountService accountService;

    public SignUpController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping(value = "/api/signup")
    public String signUp(@RequestParam(name = "mail") String mail, @RequestParam(name = "password") String password) {
        logger.info("signUp"); 
        try {
            accountService.create(mail, password);  
            return "Account created";
        } catch (IllegalArgumentException e) {
            return "Account already exists";
        }
    }
}
