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
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;

@ExtendWith(MockitoExtension.class)
public class GitlabTest {

    private static final String GITLAB_API_URL = "https://gitlab.com";

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private TechnologyScannerService technologyScannerService;

    private Gitlab gitlab;

    @BeforeEach
    void setUp() {
        gitlab = new Gitlab(GITLAB_API_URL, encryptionService, technologyScannerService);
    }

    @Test
    void getName_ShouldReturnGitLab() {
        assertEquals("GitLab", gitlab.getName());
    }

    @Test
    void getIconUrl_ShouldReturnCorrectUrl() {
        assertEquals("https://gitlab.com/favicon.ico", gitlab.getIconUrl());
    }

    @Test
    void isGit_WithValidGitlabUrl_ShouldReturnTrue() {
        assertTrue(gitlab.isGit("https://gitlab.com/username/repo"));
        assertTrue(gitlab.isGit("https://gitlab.com/group/subgroup/repo"));
        assertTrue(gitlab.isGit("http://gitlab.com/username/repo"));
    }

    @Test
    void isGit_WithInvalidUrls_ShouldReturnFalse() {
        assertFalse(gitlab.isGit("https://github.com/username/repo"));
        assertFalse(gitlab.isGit("https://bitbucket.org/username/repo"));
        assertFalse(gitlab.isGit("https://example.com"));
        assertFalse(gitlab.isGit(null));
        assertFalse(gitlab.isGit(""));
    }

    @Test
    void extractOwner_WithValidUrl_ShouldReturnOwner() {
        assertEquals("username", gitlab.extractOwner("https://gitlab.com/username/repo"));
        assertEquals("group", gitlab.extractOwner("https://gitlab.com/group/subgroup/repo"));
    }

    @Test
    void extractOwner_WithInvalidUrl_ShouldReturnNull() {
        assertNull(gitlab.extractOwner("invalid-url"));
        assertNull(gitlab.extractOwner(null));
    }

    @Test
    void extractRepoName_WithValidUrl_ShouldReturnRepoName() {
        assertEquals("repo", gitlab.extractRepoName("https://gitlab.com/username/repo"));
        assertEquals("subgroup", gitlab.extractRepoName("https://gitlab.com/group/subgroup/repo"));
    }

    @Test
    void extractRepoName_WithInvalidUrl_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> gitlab.extractRepoName("invalid-url"));
        assertNull(gitlab.extractRepoName(null));
    }

    @Test
    void gitCreatePullRequest_WithValidInput_ShouldCreateMergeRequest() throws GitPullRequestException {
        // Arrange
        Depot depot = new Depot();
        depot.setUrl("https://gitlab.com/username/repo");
        depot.setToken("test-token");
        depot.setBranch("main");

        // Act & Assert
        assertThrows(GitPullRequestException.class, () -> gitlab.gitCreatePullRequest(depot, "feature-branch"));
    }

    @Test
    void gitCreatePullRequest_WithInvalidUrl_ShouldThrowException() {
        // Arrange
        Depot depot = new Depot();
        depot.setUrl("invalid-url");
        depot.setToken("test-token");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gitlab.gitCreatePullRequest(depot, "feature-branch"));
    }
} 