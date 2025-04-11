package com.info803.dependency_manager_api.domain.technology;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.handledTechnologies.JavaTechnology;
import com.info803.dependency_manager_api.domain.technology.handledTechnologies.PHPTechnology;
import com.info803.dependency_manager_api.domain.technology.handledTechnologies.PythonTechnology;

@Service
public class TechnologyScannerService {

    private final static List<AbstractTechnology> technologies;

    static {
        technologies = new ArrayList<>();
        technologies.add(new JavaTechnology());
        technologies.add(new PythonTechnology());
        technologies.add(new PHPTechnology());
    }

    /**
     * Detects the technologies used in the given repository directory.
     *
     * @param repoPath the path to the repository directory
     * @return a map of TechnologyType to a list of file paths for each detected technology;
     *         an empty map if the directory does not exist or contains no recognizable technology files
     */
    public static List<AbstractTechnology> detectTechnologies(String repoPath) {
    
        File repoDirectory = new File(repoPath);
            
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
                    detectedTechnologies.add(technology);
                    if (!technology.getFilesPaths().contains(new File(repoPath, file).getAbsolutePath())) {
                        technology.getFilesPaths().add(new File(repoPath, file).getAbsolutePath());
                    }
                }
            }

        }

        return detectedTechnologies;
    }

    public static Map<String, List<Dependency>> detectDependencies(List<AbstractTechnology> technologies) {
        Map<String, List<Dependency>> dependenciesMap = new HashMap<>();
        for (AbstractTechnology technology : technologies) {
            List<Dependency> dependencies = new ArrayList<>();
            dependencies.addAll(technology.detectDependencies());
            dependenciesMap.put(technology.getName(), dependencies);
        }
        return dependenciesMap;
    }
}
