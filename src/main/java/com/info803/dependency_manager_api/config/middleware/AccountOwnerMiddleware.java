package com.info803.dependency_manager_api.config.middleware;

import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class AccountOwnerMiddleware implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AccountOwnerMiddleware.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        logger.info("AccountOwnerMiddleware preHandle for request URI: {}", request.getRequestURI());

        // Extract path variables
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Check if the request targets a specific account (contains 'id' path variable under /accounts/)
        if (request.getRequestURI().startsWith("/accounts/") && pathVariables != null && pathVariables.containsKey("id")) {
            String accountIdStr = pathVariables.get("id");
            Long requestedAccountId;
            try {
                requestedAccountId = Long.parseLong(accountIdStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid account ID format in path: {}", accountIdStr);
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid Account ID format.");
                return false; // Invalid ID format
            }

            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Account)) {
                logger.info("User not authenticated or authentication principal is not Account type for account access.");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Authentication required.");
                return false;
            }

            Account authenticatedAccount = (Account) authentication.getPrincipal();
            Long authenticatedAccountId = authenticatedAccount.getId();

            if (authenticatedAccountId == null) {
                 logger.error("Authenticated account ID is null. Cannot perform ownership check.");
                 response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error during authentication processing.");
                 return false;
            }

            logger.info("Checking ownership for requested Account ID: {}. Authenticated Account ID: {}", requestedAccountId, authenticatedAccountId);

            // Compare the requested account ID with the authenticated user's ID
            if (!requestedAccountId.equals(authenticatedAccountId)) {
                logger.info("Forbidden access attempt: Authenticated user ID {} does not match requested Account ID {}", authenticatedAccountId, requestedAccountId);
                response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden: You do not have permission to access this account.");
                return false; // User does not own the account
            }

            logger.info("Access granted for Account ID: {}. User ID: {}", requestedAccountId, authenticatedAccountId);
        } else {
            logger.info("Request URI {} does not match /accounts/{id}/..., skipping ownership check.", request.getRequestURI());
        }

        return true; // Proceed with the request
    }
}
