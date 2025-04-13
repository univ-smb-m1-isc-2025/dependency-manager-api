package com.info803.dependency_manager_api.config.middleware;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

@Component
public class DepotOwnerMiddleware implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DepotOwnerMiddleware.class);
    private final DepotRepository depotRepository;

    @Autowired
    public DepotOwnerMiddleware(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        logger.info("DepotOwnerInterceptor preHandle for request URI: {}", request.getRequestURI());

        // Extract path variables
        @SuppressWarnings("unchecked") // Standard way to get path variables
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Check if the request targets a specific depot (contains 'id' path variable under /depots/)
        if (request.getRequestURI().startsWith("/depots/") && pathVariables != null && pathVariables.containsKey("id")) {
            String depotIdStr = pathVariables.get("id");
            Long depotId;
            try {
                depotId = Long.parseLong(depotIdStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid depot ID format in path: {}", depotIdStr);
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid Depot ID format.");
                return false; // Invalid ID format
            }

            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Account)) {
                // Should be handled by JwtAuthenticationFilter, but double-check
                logger.info("User not authenticated or authentication principal is not Account type for depot access.");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Authentication required.");
                return false;
            }
            Account authenticatedAccount = (Account) authentication.getPrincipal();
            Long authenticatedAccountId = authenticatedAccount.getId();

             if (authenticatedAccountId == null) {
                 logger.info("Authenticated account ID is null. Cannot perform ownership check.");
                 response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error during authentication processing.");
                 return false;
             }


            // Fetch the depot
            Optional<Depot> depotOpt = depotRepository.findById(depotId);
            if (depotOpt.isEmpty()) {
                logger.warn("Depot with ID {} not found.", depotId);
                response.sendError(HttpStatus.NOT_FOUND.value(), "Depot not found.");
                return false; // Depot not found
            }

            Depot depot = depotOpt.get();
            Long depotAccountId = depot.getAccountId();

            logger.info("Checking ownership for Depot ID: {}. Depot Account ID: {}. Authenticated Account ID: {}", depotId, depotAccountId, authenticatedAccountId);

             if (depotAccountId == null) {
                 logger.warn("Depot {} has a null accountId. Cannot perform ownership check.", depotId);
                 response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: Depot configuration issue.");
                 return false;
             }

            // Compare the depot's account ID with the authenticated user's ID
            if (!depotAccountId.equals(authenticatedAccountId)) {
                logger.info("Forbidden access attempt: Authenticated user ID {} does not match Depot {} owner ID {}", authenticatedAccountId, depotId, depotAccountId);
                response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden: You do not have permission to access this depot.");
                return false; // User does not own the depot
            }

            logger.info("Access granted for Depot ID: {}. User ID: {}", depotId, authenticatedAccountId);
        } else {
            logger.info("Request URI {} does not match /depots/{id}/..., skipping ownership check.", request.getRequestURI());
        }

        return true; // Proceed with the request
    }
}