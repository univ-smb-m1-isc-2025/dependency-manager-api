package com.info803.dependency_manager_api.config;

import com.info803.dependency_manager_api.config.middleware.AccountOwnerMiddleware;
import com.info803.dependency_manager_api.config.middleware.DepotOwnerMiddleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    @Autowired
    private DepotOwnerMiddleware depotOwnerMiddleware;

    @Autowired
    private AccountOwnerMiddleware accountOwnerMiddleware;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("Registering interceptors via WebConfig...");

        // Register Depot Owner Interceptor
        registry.addInterceptor(depotOwnerMiddleware)
                .addPathPatterns("/depots/**")
                .excludePathPatterns("/depots", "/depots/create");
        logger.info("Registered DepotOwnerMiddleware for /depots/**");

        // Register Account Owner Interceptor
        registry.addInterceptor(accountOwnerMiddleware)
                .addPathPatterns("/accounts/{id}/**")
                .excludePathPatterns("/accounts/me/**");
        logger.info("Registered AccountOwnerMiddleware for /accounts/{id}/**");
    }
}