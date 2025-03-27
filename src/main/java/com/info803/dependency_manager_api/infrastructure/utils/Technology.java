package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Technology {

    private Technology() {
        // None
    }

        
    /**
     * Detects the technologies used in the given repository directory.
     *
     * @param repoPath the path to the repository directory
     * @return a map of TechnologyType to a list of file paths for each detected technology;
     *         an empty map if the directory does not exist or contains no recognizable technology files
     */
    public static Map<TechnologyType, List<String>> detectTechnologies(String repoPath) {
        File repoDirectory = new File(repoPath);
        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            return Collections.emptyMap();
        }

        File[] filesInRepo = repoDirectory.listFiles();
        if (filesInRepo == null) return Collections.emptyMap();

        Map<TechnologyType, List<String>> detectedTechnologies = new HashMap<>();

        for (TechnologyType tech : TechnologyType.values()) {
            List<String> matchedFiles = Arrays.stream(tech.getFiles())
                    .map(fileName -> new File(repoDirectory, fileName))
                    .filter(File::exists)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());

            if (!matchedFiles.isEmpty()) {
                detectedTechnologies.put(tech, matchedFiles);
            }
        }
        
        return detectedTechnologies;
    }
}
