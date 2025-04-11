package com.info803.dependency_manager_api.adapters.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorldController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // Send to the page tempLogin
    @GetMapping(value = "/auth/tempLogin")
    public String login() {
        logger.info("login");
        return "login";
    }

    @GetMapping(value = "/auth/tempRegister")
    public String register() {
        logger.info("register");
        return "register";
    }
}   
