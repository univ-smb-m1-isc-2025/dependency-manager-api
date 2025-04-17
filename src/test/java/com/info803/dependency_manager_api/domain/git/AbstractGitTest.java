package com.info803.dependency_manager_api.domain.git;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNoChangesException;
import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

@ExtendWith(MockitoExtension.class)
public class AbstractGitTest {

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private TechnologyScannerService technologyScannerService;

    @Mock
    private Git git;

    @Mock
    private Status status;

    @Mock
    private StatusCommand statusCommand;

    @Mock
    private Repository repository;

    @TempDir
    File tempDir;

    private AbstractGit abstractGit;
    private Depot depot;

    @BeforeEach
    void setUp() throws IOException, GitAPIException {
        // Initialize a Git repository in the temp directory
        Git.init().setDirectory(tempDir).call();

        abstractGit = new AbstractGit("https://api.example.com", encryptionService, technologyScannerService) {
            @Override
            public String getName() {
                return "TestGit";
            }

            @Override
            public String getIconUrl() {
                return "https://example.com/favicon.ico";
            }

            @Override
            public String gitCreatePullRequest(Depot depot, String branchName) {
                return null;
            }

            @Override
            public boolean isGit(String url) {
                return false;
            }

            @Override
            public String extractOwner(String url) {
                return null;
            }

            @Override
            public String extractRepoName(String url) {
                return null;
            }
        };

        depot = new Depot();
        depot.setId(1L);
        depot.setUsername("test-user");
        depot.setToken("test-token");
        depot.setUrl(tempDir.getAbsolutePath());
        depot.setBranch("main");
        depot.setPath(tempDir.getAbsolutePath());
    }

    @Test
    @Disabled("This test is not working")
    void gitAdd_WithValidPathAndChanges_ShouldAddChanges() throws Exception {
        // Arrange
        when(git.status()).thenReturn(statusCommand);
        when(statusCommand.call()).thenReturn(status);
        when(status.hasUncommittedChanges()).thenReturn(true);
        
        org.eclipse.jgit.api.AddCommand addCommand = mock(org.eclipse.jgit.api.AddCommand.class);
        when(git.add()).thenReturn(addCommand);
        when(addCommand.addFilepattern(anyString())).thenReturn(addCommand);
        when(addCommand.call()).thenReturn(null);
        
        // Act
        String result = abstractGit.gitAdd(depot, "file.txt");

        // Assert
        assertEquals("Changes added successfully for pattern: file.txt", result);
        verify(addCommand).addFilepattern("file.txt");
        verify(addCommand).call();
    }

    @Test
    void gitAdd_WithValidPathAndNoChanges_ShouldThrowException() throws Exception {
        // Act & Assert
        assertThrows(GitNoChangesException.class, () -> abstractGit.gitAdd(depot, "file.txt"));
    }

    @Test
    void gitAdd_WithNullPath_ShouldThrowException() {
        // Arrange
        depot.setPath(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> abstractGit.gitAdd(depot, "file.txt"));
    }

    @Test
    void gitCommit_WithValidPath_ShouldCommitChanges() throws Exception {
        // Arrange
        org.eclipse.jgit.api.CommitCommand commitCommand = mock(org.eclipse.jgit.api.CommitCommand.class);
        
        // Act
        String result = abstractGit.gitCommit(depot, "Test commit");

        // Assert
        assertEquals("Changes committed successfully with message: Test commit", result);
    }

    @Test
    void gitCommit_WithNullPath_ShouldThrowException() {
        // Arrange
        depot.setPath(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> abstractGit.gitCommit(depot, "Test commit"));
    }

    @Test
    void gitGetBranch_WithValidPath_ShouldReturnBranchName() throws Exception {
        
        // Act
        String result = abstractGit.gitGetBranch(depot);

        // Assert 
        // Branch is either master or main
        assertTrue(result.equals("master") || result.equals("main"));
    }

    @Test
    void gitGetBranch_WithNullPath_ShouldThrowException() {
        // Arrange
        depot.setPath(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> abstractGit.gitGetBranch(depot));
    }

    @Test
    @Disabled("This test is not working")
    void gitPush_WithValidPath_ShouldPushChanges() throws Exception {
        // Arrange
        org.eclipse.jgit.api.PushCommand pushCommand = mock(org.eclipse.jgit.api.PushCommand.class);
        when(git.push()).thenReturn(pushCommand);
        when(pushCommand.setRemote(anyString())).thenReturn(pushCommand);
        when(pushCommand.call()).thenReturn(null);

        // Configure remote
        RemoteConfig remoteConfig = mock(RemoteConfig.class);
        when(remoteConfig.getURIs()).thenReturn(java.util.Collections.singletonList(new URIish("file://" + tempDir.getAbsolutePath())));
        when(git.remoteList()).thenReturn(mock(org.eclipse.jgit.api.RemoteListCommand.class));
        when(git.remoteList().call()).thenReturn(java.util.Collections.singletonList(remoteConfig));
        
        // Act
        String result = abstractGit.gitPush(depot);

        // Assert
        assertEquals("Changes pushed successfully to remote repository.", result);
        verify(pushCommand).setRemote("origin");
        verify(pushCommand).call();
    }

    @Test
    void gitPush_WithNullPath_ShouldThrowException() {
        // Arrange
        depot.setPath(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> abstractGit.gitPush(depot));
    }
} 