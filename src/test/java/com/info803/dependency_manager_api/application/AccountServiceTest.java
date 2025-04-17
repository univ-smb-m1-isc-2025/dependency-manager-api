package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountEmailAlreadyInUseException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.account.AccountUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.infrastructure.persistence.account.Account;
import com.info803.dependency_manager_api.infrastructure.persistence.account.AccountRepository;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.DepotRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DepotRepository depotRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setPassword("password");
    }

    @Test
    void accountList_ShouldReturnAllAccounts() {
        // Arrange
        List<Account> expectedAccounts = Collections.singletonList(account);
        when(accountRepository.findAll()).thenReturn(expectedAccounts);

        // Act
        List<Account> result = accountService.accountList();

        // Assert
        assertEquals(expectedAccounts, result);
        verify(accountRepository).findAll();
    }

    @Test
    void account_WhenAccountExists_ShouldReturnAccount() throws AccountNotFoundException {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Act
        Optional<Account> result = accountService.account(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository).findById(1L);
    }

    @Test
    void account_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.account(1L));
        verify(accountRepository).findById(1L);
    }

    @Test
    void me_WhenAccountExists_ShouldReturnAccount() throws AccountNotFoundException {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));

        // Act
        Optional<Account> result = accountService.me("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository).findByEmail("test@example.com");
    }

    @Test
    void me_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.me("test@example.com"));
        verify(accountRepository).findByEmail("test@example.com");
    }

    @Test
    void delete_WhenAccountExists_ShouldDeleteAccount() throws AccountDeleteException, AccountNotFoundException {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Act
        accountService.delete(1L);

        // Assert
        verify(accountRepository).findById(1L);
        verify(accountRepository).delete(account);
    }

    @Test
    void delete_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.delete(1L));
        verify(accountRepository).findById(1L);
    }

    @Test
    void update_WhenAccountExistsAndEmailNotInUse_ShouldUpdateAccount() throws AccountUpdateException, AccountEmailAlreadyInUseException, AccountNotFoundException {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setEmail("new@example.com");
        updatedAccount.setPassword("new-password");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");
        when(accountRepository.save(any())).thenReturn(account);

        // Act
        Account result = accountService.update(1L, updatedAccount);

        // Assert
        assertEquals(account, result);
        verify(accountRepository).findById(1L);
        verify(accountRepository).findByEmail("new@example.com");
        verify(passwordEncoder).encode("new-password");
        verify(accountRepository).save(account);
    }

    @Test
    void update_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        Account updatedAccount = new Account();
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.update(1L, updatedAccount));
        verify(accountRepository).findById(1L);
    }

    @Test
    void update_WhenEmailAlreadyInUse_ShouldThrowException() {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setEmail("existing@example.com");

        Account existingAccount = new Account();
        existingAccount.setId(2L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingAccount));

        // Act & Assert
        assertThrows(AccountEmailAlreadyInUseException.class, () -> accountService.update(1L, updatedAccount));
        verify(accountRepository).findById(1L);
        verify(accountRepository).findByEmail("existing@example.com");
    }

    @Test
    void accountDepots_WhenAccountExists_ShouldReturnDepots() throws AccountNotFoundException, DepotNotFoundException {
        // Arrange
        List<Depot> expectedDepots = Collections.singletonList(new Depot());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(depotRepository.findByAccountId(1L)).thenReturn(expectedDepots);

        // Act
        List<Depot> result = accountService.accountDepots(1L);

        // Assert
        assertEquals(expectedDepots, result);
        verify(accountRepository).findById(1L);
        verify(depotRepository).findByAccountId(1L);
    }

    @Test
    void accountDepots_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.accountDepots(1L));
        verify(accountRepository).findById(1L);
    }
} 