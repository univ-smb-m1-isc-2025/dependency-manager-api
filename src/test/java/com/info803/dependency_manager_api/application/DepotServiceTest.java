package com.info803.dependency_manager_api.application;

import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotCreationException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitActionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitBranchException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCloneException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCodeException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.git.GitScannerService;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
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

import javax.security.auth.login.AccountNotFoundException;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepotServiceTest {

    @Mock
    private DepotRepository depotRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private GitScannerService gitScannerService;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private DepotService depotService;

    private Depot depot;
    private Account account;
    private AbstractGit abstractGit;

    @BeforeEach
    void setUp() {
        depot = new Depot();
        depot.setId(1L);
        depot.setUrl("https://github.com/test/repo");
        depot.setToken("test-token");
        depot.setAccountId(1L);

        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");

        abstractGit = mock(AbstractGit.class);
    }

    @Test
    void depotList_ShouldReturnAllDepots() {
        // Arrange
        List<Depot> expectedDepots = Collections.singletonList(depot);
        when(depotRepository.findAll()).thenReturn(expectedDepots);

        // Act
        List<Depot> result = depotService.depotList();

        // Assert
        assertEquals(expectedDepots, result);
        verify(depotRepository).findAll();
    }

    @Test
    void depot_WhenDepotExists_ShouldReturnDepot() throws DepotNotFoundException {
        // Arrange
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));

        // Act
        Optional<Depot> result = depotService.depot(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(depot, result.get());
        verify(depotRepository).findById(1L);
    }

    @Test
    void depot_WhenDepotDoesNotExist_ShouldThrowException() {
        // Arrange
        when(depotRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepotNotFoundException.class, () -> depotService.depot(1L));
        verify(depotRepository).findById(1L);
    }

    @Test
    void create_WhenAccountExists_ShouldCreateDepot() throws AccountNotFoundException, DepotCreationException, GitActionException, GitNotFoundException, GitBranchException, GitCloneException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        Depot savedDepot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        savedDepot.setId(1L);
        
        Account account = new Account();
        account.setId(1L);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.getIconUrl()).thenReturn("test-icon-url");
        when(encryptionService.encrypt("token")).thenReturn("encrypted-token");
        when(depotRepository.save(any(Depot.class))).thenReturn(savedDepot);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(savedDepot));
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("Clone successful", "main");

        // Act
        Depot result = depotService.create(depot);

        // Assert
        assertEquals(savedDepot, result);
        verify(accountRepository).findById(1L);
        verify(gitScannerService, times(3)).detectGit(depot.getUrl());
        verify(encryptionService).encrypt("token");
        verify(depotRepository).save(depot);
    }

    @Test
    void create_WhenAccountDoesNotExist_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> depotService.create(depot));
        verify(accountRepository).findById(1L);
    }

    @Test
    void update_WhenDepotExists_ShouldUpdateDepot() throws DepotNotFoundException, DepotUpdateException, GitNotFoundException {
        // Arrange
        Depot updatedDepot = new Depot();
        updatedDepot.setUrl("https://github.com/test/updated-repo");
        updatedDepot.setToken("updated-token");

        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(any())).thenReturn(abstractGit);
        when(abstractGit.getIconUrl()).thenReturn("updated-icon-url");
        when(encryptionService.encrypt(any())).thenReturn("encrypted-token");
        when(depotRepository.save(any())).thenReturn(depot);

        // Act
        Depot result = depotService.update(1L, updatedDepot);

        // Assert
        assertEquals(depot, result);
        verify(depotRepository).findById(1L);
        verify(gitScannerService).detectGit(updatedDepot.getUrl());
        verify(encryptionService).encrypt(updatedDepot.getToken());
        verify(depotRepository).save(depot);
    }

    @Test
    void update_WhenDepotDoesNotExist_ShouldThrowException() {
        // Arrange
        Depot updatedDepot = new Depot();
        when(depotRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepotNotFoundException.class, () -> depotService.update(1L, updatedDepot));
        verify(depotRepository).findById(1L);
    }

    @Test
    void delete_WhenDepotExists_ShouldDeleteDepot() throws DepotNotFoundException {
        // Arrange
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));

        // Act
        depotService.delete(1L);

        // Assert
        verify(depotRepository).findById(1L);
        verify(depotRepository).delete(depot);
    }

    @Test
    void delete_WhenDepotDoesNotExist_ShouldThrowException() {
        // Arrange
        when(depotRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DepotNotFoundException.class, () -> depotService.delete(1L));
        verify(depotRepository).findById(1L);
    }

    @Test
    void updateLastDependenciesUpdateDate_ShouldUpdateDate() throws DepotUpdateException, DepotNotFoundException {
        // Arrange
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(depotRepository.save(any())).thenReturn(depot);

        // Act
        String result = depotService.updateLastDependenciesUpdateDate(1L);

        // Assert
        assertNotNull(result);
        verify(depotRepository).findById(1L);
        verify(depotRepository).save(depot);
    }

    @Test
    void gitClone_ShouldCloneRepository() throws GitCloneException, DepotNotFoundException, GitActionException, GitNotFoundException, GitBranchException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        depot.setId(1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("Clone successful", "main");

        // Act
        depotService.gitClone(1L);

        // Assert
        verify(depotRepository, times(4)).findById(1L);
        verify(abstractGit, times(2)).executeGitAction(any(Depot.class), any());
    }

    @Test
    void gitDelete_ShouldDeleteRepository() throws GitDeleteException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("Delete successful");

        // Act
        depotService.gitDelete(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
    }

    @Test
    void gitPull_ShouldPullRepository() throws GitPullException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("Pull successful");

        // Act
        depotService.gitPull(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
    }

    @Test
    void gitCode_ShouldReturnFiles() throws GitCodeException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn(new ArrayList<File>());

        // Act
        List<File> result = depotService.gitCode(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
        assertNotNull(result);
    }

    @Test
    void gitCodeTechnologies_ShouldReturnTechnologies() throws GitCodeException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn(new ArrayList<AbstractTechnology>());

        // Act
        List<AbstractTechnology> result = depotService.gitCodeTechnologies(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
        assertNotNull(result);
    }

    @Test
    void gitCodeDependencies_ShouldReturnDependencies() throws GitCodeException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn(new HashMap<String, List<Dependency>>());

        // Act
        Map<String, List<Dependency>> result = depotService.gitCodeDependencies(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
        assertNotNull(result);
    }

    @Test
    void gitGetBranch_ShouldReturnBranch() throws GitBranchException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("main");

        // Act
        String result = depotService.gitGetBranch(1L);

        // Assert
        verify(depotRepository, times(2)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
        assertEquals("main", result);
    }

    @Test
    void gitPullRequest_ShouldCreatePullRequest() throws GitPullRequestException, DepotNotFoundException, GitActionException, GitNotFoundException {
        // Arrange
        Depot depot = new Depot("test-depot", "https://github.com/test/repo", "username", "token", 1L);
        when(depotRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(gitScannerService.detectGit(anyString())).thenReturn(abstractGit);
        when(abstractGit.executeGitAction(any(Depot.class), any())).thenReturn("PR created");

        // Act
        depotService.gitPullRequest(1L);

        // Assert
        verify(depotRepository, times(3)).findById(1L);
        verify(abstractGit).executeGitAction(any(Depot.class), any());
    }
} 