package com.info803.dependency_manager_api.domain.git;

import java.util.HashMap;
import java.util.Map;

import com.info803.dependency_manager_api.domain.git.handledGit.*;

public class GitDetector {
    private Map<String, AbstractGit> gitWiki = new HashMap<String, AbstractGit>();

    public GitDetector() {
        // Github
        gitWiki.put("https://github.com", new Github());

        // Gitlab
        gitWiki.put("https://gitlab.com", new Gitlab());

        // Bitbucket
        gitWiki.put("https://bitbucket.org", new Bitbucket()); 
    }

    public AbstractGit detectGit(String url) {
        for (Map.Entry<String, AbstractGit> entry : gitWiki.entrySet()) {
            if (url.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
