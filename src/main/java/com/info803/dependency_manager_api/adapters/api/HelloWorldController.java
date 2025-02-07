package com.info803.dependency_manager_api.adapters.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/api/hello-world")
    public String helloWorld() {
        logger.info("helloWorld");
        return "Hello World";
    }
}   
