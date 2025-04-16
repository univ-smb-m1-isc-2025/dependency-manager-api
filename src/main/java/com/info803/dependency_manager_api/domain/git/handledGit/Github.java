package com.info803.dependency_manager_api.domain.git.handledGit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class Github extends AbstractGit {
    
    public Github(@Value("${github.api.url:https://api.github.com}") String gitApiUrl,
                   EncryptionService encryptionService,
                   TechnologyScannerService technologyScannerService) {
        super(gitApiUrl, encryptionService, technologyScannerService);
    }

    @Override
    public String getName() {
        return "GitHub";
    }

    @Override
    public String getIconUrl() {
        return "https://github.com/favicon.ico";
    }

    @Override
    public String gitCreatePullRequest(Depot depot, String newBranchName) {
        try {
            String owner = extractOwner(depot.getUrl());
            String repoName = extractRepoName(depot.getUrl());
            if (owner == null || repoName == null) {
                 throw new IllegalArgumentException("Could not extract owner or repository name from URL: " + depot.getUrl());
            }
            String pullRequestBranch = newBranchName;
            String baseBranch = depot.getBranch();
            String prTitle = "Dependency Manager: Update Dependencies";
            String prBody = "Automated pull request to update project dependencies.";
            String token = depot.getToken();

            GitHub github = new GitHubBuilder().withEndpoint(this.gitApiUrl).withOAuthToken(token).build();
            GHRepository repo = github.getRepository(owner + "/" + repoName);

            try (Git git = Git.open(new File(depot.getPath()))) {
                if (git.branchList().call().stream().noneMatch(ref -> ref.getName().endsWith("/" + pullRequestBranch))) {
                     throw new RuntimeException("Source branch '" + pullRequestBranch + "' does not exist locally in repository path: " + depot.getPath() +". Local branches : " + git.branchList().call().stream().map(ref -> ref.getName()).collect(Collectors.joining(", ")));
                }
            } catch (GitAPIException | IOException e) {
                throw new RuntimeException("Failed to verify local branches: " + e.getMessage(), e);
            }

            repo.createPullRequest(prTitle, pullRequestBranch, baseBranch, prBody);

            return "Pull request created successfully on GitHub for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.";
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during pull request creation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isGit(String url) {
        return url != null && url.contains("github.com");
    }

    @Override
    public String extractOwner(String url) {
        if (url == null) return null;
        String path = url.replaceFirst("https?://github.com/", "");
        String[] parts = path.split("/");
        return (parts.length > 0) ? parts[0] : null;
    }

    @Override
    public String extractRepoName(String url) {
        if (url == null) return null;
        String path = url.replaceFirst("https?://github.com/", "");
        String[] parts = path.split("/");
        if (parts.length > 1) {
            String repoPart = parts[1];
            return repoPart.endsWith(".git") ? repoPart.substring(0, repoPart.length() - 4) : repoPart;
        }
        return null;
    }
}
