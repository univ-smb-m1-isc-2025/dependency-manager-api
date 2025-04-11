package com.info803.dependency_manager_api.domain.git.handledGit;

import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class Github extends AbstractGit {
 
    @Override
    public String gitPullRequest(Depot depot) {
        String path = depot.getPath();
        String branch = depot.getBranch();
        String branchName = generateBranchName(depot);
        try {
            // Create a new branch
            gitCreateBranch(depot);

            // Add the changes to the branch
            gitAdd(depot, ".");
            // Commit the changes
            gitCommit(depot, "Commit changes");

            // Push the changes to the branch
            gitPush(depot);
            
            return "Pull request created";
        } catch (Exception e) {
            throw new RuntimeException("Error during git pull request: " + e.getMessage(), e);
        }
    }
}
