package com.info803.dependency_manager_api.domain.git.handledGit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

@ExtendWith(MockitoExtension.class)
public class GithubTest {

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private TechnologyScannerService technologyScannerService;

    private Github github;

    @BeforeEach
    void setUp() {
        github = new Github("https://api.github.com", encryptionService, technologyScannerService);
    }

    @Test
    void getName_ShouldReturnGithub() {
        assertEquals("GitHub", github.getName());
    }

    @Test
    void getIconUrl_ShouldReturnGithubFavicon() {
        assertEquals("https://github.com/favicon.ico", github.getIconUrl());
    }

    @Test
    void isGit_WhenGithubUrl_ShouldReturnTrue() {
        assertTrue(github.isGit("https://github.com/test/repo"));
    }

    @Test
    void isGit_WhenNotGithubUrl_ShouldReturnFalse() {
        assertFalse(github.isGit("https://gitlab.com/test/repo"));
    }

    @Test
    void isGit_WhenNullUrl_ShouldReturnFalse() {
        assertFalse(github.isGit(null));
    }

    @Test
    void extractOwner_WhenValidUrl_ShouldReturnOwner() {
        assertEquals("test", github.extractOwner("https://github.com/test/repo"));
    }

    @Test
    void extractOwner_WhenValidUrlWithGitExtension_ShouldReturnOwner() {
        assertEquals("test", github.extractOwner("https://github.com/test/repo.git"));
    }

    @Test
    void extractOwner_WhenInvalidUrl_ShouldReturnNull() {
        assertNull(github.extractOwner("invalid-url"));
    }

    @Test
    void extractOwner_WhenNullUrl_ShouldReturnNull() {
        assertNull(github.extractOwner(null));
    }

    @Test
    void extractRepoName_WhenValidUrl_ShouldReturnRepoName() {
        assertEquals("repo", github.extractRepoName("https://github.com/test/repo"));
    }

    @Test
    void extractRepoName_WhenValidUrlWithGitExtension_ShouldReturnRepoName() {
        assertEquals("repo", github.extractRepoName("https://github.com/test/repo.git"));
    }

    @Test
    void extractRepoName_WhenInvalidUrl_ShouldReturnNull() {
        assertNull(github.extractRepoName("invalid-url"));
    }

    @Test
    void extractRepoName_WhenNullUrl_ShouldReturnNull() {
        assertNull(github.extractRepoName(null));
    }

    @Test
    void executeGitAction_ShouldDecryptAndEncryptToken() throws Exception {
        // Arrange
        Depot depot = new Depot();
        depot.setToken("encrypted-token");
        when(encryptionService.decrypt("encrypted-token")).thenReturn("decrypted-token");
        when(encryptionService.encrypt("decrypted-token")).thenReturn("re-encrypted-token");

        // Act
        github.executeGitAction(depot, d -> "result");

        // Assert
        verify(encryptionService).decrypt("encrypted-token");
        verify(encryptionService).encrypt("decrypted-token");
        assertEquals("re-encrypted-token", depot.getToken());
    }
} 