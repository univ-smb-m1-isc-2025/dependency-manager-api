package com.info803.dependency_manager_api.adapters.api.exception;


import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationAuthenticateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.auth.AuthenticationRegisterException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyFetchingException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotCreationException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitActionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitAddException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitBranchException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCheckoutException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCloneException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCodeException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCommitException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNoChangesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPushException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.adapters.api.response.ResponseUtil;

import jakarta.ws.rs.NotFoundException;

import javax.security.auth.login.AccountException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // ----------------------------
    // ---- General Exceptions ----
    // ----------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage(), ex);
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(ResponseUtil.error(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // -----------------------------------
    // ---- Authentication Exceptions ----
    // -----------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // -----------------------------------
    // ---- Authentication Exceptions ----
    // -----------------------------------

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(BadCredentialsException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationRegisterException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationRegisterException(AuthenticationRegisterException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationAuthenticateException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationAuthenticatedException(AuthenticationAuthenticateException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    // ----------------------------
    // ---- Account Exceptions ----
    // -----------------------------

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountException(AccountException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountNotFoundException(AccountNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountEmailAlreadyInUseException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountEmailAlreadyInUseException(AccountEmailAlreadyInUseException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountUpdateException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountUpdateException(AccountUpdateException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    // -------------------------
    // --- Depot Exceptions ----
    // -------------------------

    @ExceptionHandler(DepotException.class)
    public ResponseEntity<ApiResponse<String>> handleDepotException(DepotException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DepotNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleDepotNotFoundException(DepotNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DepotCreationException.class)
    public ResponseEntity<ApiResponse<String>> handleDepotCreationException(DepotCreationException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DepotUpdateException.class)
    public ResponseEntity<ApiResponse<String>> handleDepotUpdateException(DepotUpdateException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);    
    }


    // ------------------------
    // ---- Git Exceptions ----
    // ------------------------

    @ExceptionHandler(GitNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGitNotFoundException(GitNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GitCloneException.class)
    public ResponseEntity<ApiResponse<String>> handleGitCloneException(GitCloneException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitPullException.class)
    public ResponseEntity<ApiResponse<String>> handleGitPullException(GitPullException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitPullRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleGitPullRequestException(GitPullRequestException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }   

    @ExceptionHandler(GitCommitException.class)
    public ResponseEntity<ApiResponse<String>> handleGitCommitException(GitCommitException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitPushException.class)
    public ResponseEntity<ApiResponse<String>> handleGitPushException(GitPushException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitCodeException.class)
    public ResponseEntity<ApiResponse<String>> handleGitCodeException(GitCodeException ex) {
        logger.error(ex.getMessage(), ex);        
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitAddException.class)
    public ResponseEntity<ApiResponse<String>> handleGitAddException(GitAddException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitDeleteException.class)
    public ResponseEntity<ApiResponse<String>> handleGitDeleteException(GitDeleteException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitBranchException.class)
    public ResponseEntity<ApiResponse<String>> handleGitBranchException(GitBranchException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitActionException.class)
    public ResponseEntity<ApiResponse<String>> handleGitActionException(GitActionException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitNoChangesException.class)
    public ResponseEntity<ApiResponse<String>> handleGitNoChangesException(GitNoChangesException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(GitCheckoutException.class)
    public ResponseEntity<ApiResponse<String>> handleGitCheckoutException(GitCheckoutException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleRepositoryNotFoundException(RepositoryNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    // --------------------------------
    // ----- Dependency Exceptions ----
    // --------------------------------

    @ExceptionHandler(DependencyDetectLatestVersionException.class)
    public ResponseEntity<ApiResponse<String>> handleDependencyDetectLatestVersionException(DependencyDetectLatestVersionException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DependencyFetchingException.class)
    public ResponseEntity<ApiResponse<String>> handleDependencyFetchingException(DependencyFetchingException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // -------------------------------
    // ---- Technology Exceptions ----
    // -------------------------------
    
    @ExceptionHandler(TechnologyExtractDependenciesException.class)
    public ResponseEntity<ApiResponse<String>> handleExtractDependenciesException(TechnologyExtractDependenciesException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TechnologyUpdateDependenciesException.class)
    public ResponseEntity<ApiResponse<String>> handleUpdateDependenciesException(TechnologyUpdateDependenciesException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse<String> response = ResponseUtil.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}