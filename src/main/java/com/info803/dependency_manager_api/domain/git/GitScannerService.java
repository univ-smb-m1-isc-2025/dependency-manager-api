package com.info803.dependency_manager_api.domain.git;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GitScannerService {

    public List<AbstractGit> gits;

    public GitScannerService(List<AbstractGit> gits) {
        this.gits = gits;
    }

    public AbstractGit detectGit(String url) {
        for (AbstractGit git : gits) {
            if (git.isGit(url)) {
                return git;
            }
        }
        return null;
    }
    
    
}
