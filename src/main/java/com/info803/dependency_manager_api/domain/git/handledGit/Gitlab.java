package com.info803.dependency_manager_api.domain.git.handledGit;

import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

import jakarta.ws.rs.NotFoundException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class Gitlab extends AbstractGit {

    public Gitlab(@Value("${gitlab.api.url:https://gitlab.com}") String gitApiUrl,
                   EncryptionService encryptionService,
                   TechnologyScannerService technologyScannerService) {
        super(gitApiUrl, encryptionService, technologyScannerService);
    }

    @Override
    public String getName() {
        return "GitLab";
    }

    @Override
    public String getIconUrl() {
        return "https://gitlab.com/favicon.ico";
    }

    @Override
    public boolean isGit(String url) {
        return url != null && url.contains("gitlab.com");
    }

    @Override
    public String extractOwner(String url)  {
        if (!isGit(url)) {
            return null;
        }
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath().substring(1); // Remove leading /
            if (path.endsWith(".git")) {
                path = path.substring(0, path.length() - 4);
            }
            String[] parts = path.split("/");
            return (parts.length > 1) ? parts[0] : null; // Namespace is the first part
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error parsing GitLab URL: " + url + "; " + e.getMessage());
        }
    }

    @Override
    public String extractRepoName(String url) {
        // GitLab uses namespace/project-name, repo name is the project-name part
         if (url == null) return null;
         try {
             URL parsedUrl = new URL(url);
             String path = parsedUrl.getPath().substring(1); // Remove leading /
             if (path.endsWith(".git")) {
                 path = path.substring(0, path.length() - 4);
             }
             String[] parts = path.split("/");
             return (parts.length > 1) ? parts[1] : null; // Project name is the second part
         } catch (MalformedURLException e) {
            throw new RuntimeException("Error parsing GitLab URL: " + url + "; " + e.getMessage());
         }
    }

    @Override
    public String gitCreatePullRequest(Depot depot, String newBranchName) throws GitPullRequestException {
        String owner = extractOwner(depot.getUrl());
        String repoName = extractRepoName(depot.getUrl());
        if (owner == null || repoName == null) {
            throw new IllegalArgumentException("Could not extract owner/repo name from URL: " + depot.getUrl());
        }
        String namespacedPath = owner + "/" + repoName;

        String pullRequestBranch = newBranchName;
        String baseBranch = depot.getBranch();
        String mrTitle = "Dependency Manager: Update Dependencies";
        String mrDescription = "Automated merge request to update project dependencies.";
        String token = depot.getToken();

        GitLabApi gitLabApi = new GitLabApi(this.gitApiUrl, token);

        try {
            // Find the project using the namespaced path
            Project project = gitLabApi.getProjectApi().getProject(namespacedPath);
            if (project == null) {
                throw new NotFoundException("Project not found: " + namespacedPath);
            }
            // Verify local branch exists
            if (!verifyLocalBranchExists(depot, pullRequestBranch)) {
                throw new GitPullRequestException("Source branch '" + pullRequestBranch + "' does not exist locally in repository path: " + depot.getPath());
            }

            return createMergeRequest(gitLabApi, project, pullRequestBranch, baseBranch, mrTitle, mrDescription);
        } catch (Exception e) {
            throw new GitPullRequestException("An error occurred during merge request creation: " + e.getMessage(), e);
        } finally {
            gitLabApi.close();
        }
    }

    // -- Private methods --
    private boolean verifyLocalBranchExists(Depot depot, String pullRequestBranch) throws GitPullRequestException {
        try (Git git = Git.open(new File(depot.getPath()))) {
            return git.branchList().call().stream().anyMatch(ref -> ref.getName().endsWith("/" + pullRequestBranch));
        } catch (GitAPIException | IOException e) {
            throw new GitPullRequestException("Error during git branch check: " + e.getMessage(), e);
        }
    }

    private String createMergeRequest(GitLabApi gitLabApi, Project project, String pullRequestBranch, String baseBranch, String mrTitle, String mrDescription) throws GitPullRequestException {
        try {
            MergeRequest mergeRequest = gitLabApi.getMergeRequestApi()
                .createMergeRequest(project.getId(), pullRequestBranch, baseBranch, mrTitle, mrDescription, null);
    
            return "Merge request created successfully on GitLab (ID: " + mergeRequest.getIid() + ") for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.";
    
        } catch (GitLabApiException e) {
            if (e.getMessage().contains("Merge request already exists")) {
                throw new GitPullRequestException("Merge request already exists for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.");
            } else {
                throw new GitPullRequestException("Failed to create GitLab merge request: " + e.getMessage() + " for branch '" + pullRequestBranch + "' into '" + baseBranch + "'.", e);
            }
        }
    }
}
