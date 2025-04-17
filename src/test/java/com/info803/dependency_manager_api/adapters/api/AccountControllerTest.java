package com.info803.dependency_manager_api.adapters.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.application.AccountService;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private AccountController accountController;

    @BeforeEach
    void setUp() {
        accountController = new AccountController(accountService);
    }

    @Test
    void accountList_ShouldReturnListOfAccounts() {
        // Arrange
        Account account1 = new Account();
        account1.setId(1L);
        account1.setEmail("test1@example.com");

        Account account2 = new Account();
        account2.setId(2L);
        account2.setEmail("test2@example.com");

        List<Account> accounts = Arrays.asList(account1, account2);
        when(accountService.accountList()).thenReturn(accounts);

        // Act
        ResponseEntity<ApiResponse<List<Account>>> response = accountController.accountList();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Accounts retrieved", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(accounts, response.getBody().getData());
    }

    @Test
    void account_WithValidId_ShouldReturnAccount() throws AccountNotFoundException {
        // Arrange
        Account account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");

        when(accountService.account(1L)).thenReturn(Optional.of(account));

        // Act
        ResponseEntity<ApiResponse<Optional<Account>>> response = accountController.account(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Account retrieved", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isPresent());
        assertEquals(1L, response.getBody().getData().get().getId());
        assertEquals("test@example.com", response.getBody().getData().get().getEmail());
    }

    @Test
    void account_WithInvalidId_ShouldThrowException() throws AccountNotFoundException {
        // Arrange
        when(accountService.account(999L)).thenThrow(new AccountNotFoundException("Account not found"));

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> {
            accountController.account(999L);
        });
    }

    @Test
    void update_WithValidData_ShouldUpdateAccount() throws AccountUpdateException, AccountEmailAlreadyInUseException, AccountNotFoundException {
        // Arrange
        Account account = new Account("test@example.com", "password");
        account.setId(1L);
        Account updatedAccount = new Account("updated@example.com", "newpassword");
        updatedAccount.setId(1L);
        when(accountService.update(1L, account)).thenReturn(updatedAccount);

        // Act
        ResponseEntity<ApiResponse<Account>> response = accountController.update(1L, account);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Account updated", response.getBody().getMessage());
        assertEquals(updatedAccount, response.getBody().getData());
    }

    @Test
    void accountDepots_WithValidId_ShouldReturnDepots() throws AccountNotFoundException, DepotNotFoundException {
        // Arrange
        Depot depot1 = new Depot("Depot1", "url1", "user1", "token1", 1L);
        depot1.setId(1L);
        Depot depot2 = new Depot("Depot2", "url2", "user2", "token2", 1L);
        depot2.setId(2L);
        List<Depot> depots = Arrays.asList(depot1, depot2);
        when(accountService.accountDepots(1L)).thenReturn(depots);

        // Act
        ResponseEntity<ApiResponse<List<Depot>>> response = accountController.accountDepots(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Account depots retrieved", response.getBody().getMessage());
        assertEquals(depots, response.getBody().getData());
    }
} 