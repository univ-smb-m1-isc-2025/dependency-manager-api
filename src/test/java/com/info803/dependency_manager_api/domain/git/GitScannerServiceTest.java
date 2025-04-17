package com.info803.dependency_manager_api.domain.git;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.info803.dependency_manager_api.domain.git.handledGit.Github;
import com.info803.dependency_manager_api.domain.git.handledGit.Gitlab;

@ExtendWith(MockitoExtension.class)
public class GitScannerServiceTest {

    @Mock
    private Github github;

    @Mock
    private Gitlab gitlab;

    private GitScannerService gitScannerService;

    @BeforeEach
    void setUp() {
        List<AbstractGit> gits = Arrays.asList(github, gitlab);
        gitScannerService = new GitScannerService(gits);
    }

    @Test
    void detectGit_WhenGithubUrl_ShouldReturnGithub() {
        // Arrange
        String url = "https://github.com/test/repo";
        when(github.isGit(url)).thenReturn(true);

        // Act
        AbstractGit result = gitScannerService.detectGit(url);

        // Assert
        assertSame(github, result);
        verify(github).isGit(url);
        verifyNoMoreInteractions(github, gitlab);
    }

    @Test
    void detectGit_WhenGitlabUrl_ShouldReturnGitlab() {
        // Arrange
        String url = "https://gitlab.com/test/repo";
        when(github.isGit(url)).thenReturn(false);
        when(gitlab.isGit(url)).thenReturn(true);

        // Act
        AbstractGit result = gitScannerService.detectGit(url);

        // Assert
        assertSame(gitlab, result);
        verify(github).isGit(url);
        verify(gitlab).isGit(url);
        verifyNoMoreInteractions(github, gitlab);
    }

    @Test
    void detectGit_WhenUnsupportedUrl_ShouldReturnNull() {
        // Arrange
        String url = "https://bitbucket.org/test/repo";
        when(github.isGit(url)).thenReturn(false);
        when(gitlab.isGit(url)).thenReturn(false);

        // Act
        AbstractGit result = gitScannerService.detectGit(url);

        // Assert
        assertNull(result);
        verify(github).isGit(url);
        verify(gitlab).isGit(url);
        verifyNoMoreInteractions(github, gitlab);
    }

    @Test
    void detectGit_WhenNullUrl_ShouldReturnNull() {
        // Arrange
        String url = null;
        when(github.isGit(url)).thenReturn(false);
        when(gitlab.isGit(url)).thenReturn(false);

        // Act
        AbstractGit result = gitScannerService.detectGit(url);

        // Assert
        assertNull(result);
        verify(github).isGit(url);
        verify(gitlab).isGit(url);
        verifyNoMoreInteractions(github, gitlab);
    }

    @Test
    void constructor_WhenEmptyGitList_ShouldCreateServiceWithEmptyList() {
        // Arrange & Act
        GitScannerService service = new GitScannerService(Collections.emptyList());

        // Assert
        assertTrue(service.gits.isEmpty());
    }
} 