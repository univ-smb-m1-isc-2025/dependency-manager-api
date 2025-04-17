package com.info803.dependency_manager_api.domain.technology;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyFetchingException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;

@Service
public class TechnologyScannerService {

    private final List<AbstractTechnology> technologies;

    public TechnologyScannerService(List<AbstractTechnology> technologies) {
        this.technologies = technologies;
    }

    /**
     * Detects the technologies used in the given repository directory.
     *
     * @param repoPath the path to the repository directory
     * @return a map of TechnologyType to a list of file paths for each detected technology;
     *         an empty map if the directory does not exist or contains no recognizable technology files
     */
    public List<AbstractTechnology> detectTechnologies(String repoPath) {
    
        File repoDirectory = new File(repoPath);

        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            throw new RuntimeException("Cloned repository not found.");
        }
            
        List<AbstractTechnology> detectedTechnologies = new ArrayList<>();
    

        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            return detectedTechnologies;
        }
    
        File[] filesInRepo = repoDirectory.listFiles();

        if (filesInRepo == null) {
            return detectedTechnologies;
        }
    
        for (AbstractTechnology technology : technologies) {
            for (String file : technology.getFilesNames()) {
                if (new File(repoPath, file).exists() && new File(repoPath, file).isFile()) {
                    AbstractTechnology technologyCopy = technology.copy();
                    detectedTechnologies.add(technologyCopy);
                    if (!technologyCopy.getFilesPaths().contains(new File(repoPath, file).getAbsolutePath())) {
                        technologyCopy.getFilesPaths().add(new File(repoPath, file).getAbsolutePath());
                    }
                }
            }

        }

        return detectedTechnologies;
    }

    public Map<String, List<Dependency>> detectDependencies(List<AbstractTechnology> technologies) throws DependencyDetectLatestVersionException, DependencyFetchingException, TechnologyExtractDependenciesException {
        Map<String, List<Dependency>> dependenciesMap = new HashMap<>();
        for (AbstractTechnology technology : technologies) {
            List<Dependency> dependencies = new ArrayList<>();
            dependencies.addAll(technology.detectDependencies());
            dependenciesMap.put(technology.getName(), dependencies);
        }
        return dependenciesMap;
    }
}
