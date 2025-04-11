package com.info803.dependency_manager_api.domain.git.handledGit;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import com.info803.dependency_manager_api.domain.git.AbstractGit;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;

public class Gitlab extends AbstractGit {
    
    @Override
    public String gitPullRequest(Depot depot) {
        // TODO: Implement git pull request for Gitlab
        return null;
    }
}
