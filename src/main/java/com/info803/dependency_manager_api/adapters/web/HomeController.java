package com.info803.dependency_manager_api.adapters.web;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class HomeController {
    private final Logger logger = getLogger(this.getClass());

    @GetMapping(value = "/")
    public String home() {
        logger.info("home");
        return "home";
    }
}
