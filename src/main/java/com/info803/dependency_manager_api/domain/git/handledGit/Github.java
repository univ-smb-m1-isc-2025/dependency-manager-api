package com.info803.dependency_manager_api.domain.git.handledGit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
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
    public String gitCreatePullRequest(Depot depot, String newBranchName) throws GitPullRequestException {
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

            if (!checkBranchExists(depot, pullRequestBranch)) {
                throw new GitPullRequestException("Source branch '" + pullRequestBranch + "' does not exist locally in repository path: " + depot.getPath());
            }

            return createMergeRequest(repo, pullRequestBranch, baseBranch, prTitle, prBody);
        } catch (Exception e) {
            throw new GitPullRequestException("An error occurred during pull request creation: " + e.getMessage(), e);
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

    // -- Private methods --
    private boolean checkBranchExists(Depot depot, String pullRequestBranch) throws GitPullRequestException {
        try (Git git = Git.open(new File(depot.getPath()))) {
            return git.branchList().call().stream().anyMatch(ref -> ref.getName().endsWith("/" + pullRequestBranch));
        } catch (GitAPIException | IOException e) {
            throw new GitPullRequestException("Error during git branch check: " + e.getMessage(), e);
        }
    }

    private String createMergeRequest(GHRepository repo, String pullRequestBranch, String baseBranch, String mrTitle, String mrDescription) throws GitPullRequestException {
        try {
            repo.createPullRequest(mrTitle, pullRequestBranch, baseBranch, mrDescription);
            return "Pull request created successfully on GitHub for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.";
        } catch (Exception e) {
            throw new GitPullRequestException("Failed to create GitHub pull request: " + e.getMessage() + " for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.", e);
        }
    }
}
