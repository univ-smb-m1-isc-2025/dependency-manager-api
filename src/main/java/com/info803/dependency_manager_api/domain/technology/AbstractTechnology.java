package com.info803.dependency_manager_api.domain.technology;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyFetchingException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractTechnology {

    // Attributes
    protected String name;
    protected String dependencyKey;
    protected List<String> filesNames;
    protected List<String> filesPaths;


    // Constructors
    public AbstractTechnology(String name, String dependencyKey, List<String> files) {
        this.name = name;
        this.dependencyKey = dependencyKey;
        this.filesNames = files;
        this.filesPaths = new ArrayList<>();
    }

    // Methods
    /** 
     * Detects the dependencies in the files given by the filesPaths attribute
     * @return a list of dependencies
     * @throws RuntimeException if there is an error while reading the file
     * @throws DependencyDetectLatestVersionException if there is an error while detecting the latest version of a dependency
     * @throws DependencyFetchingException if there is an error while fetching the latest version of a dependency
     * @throws TechnologyExtractDependenciesException 
     * */
    public List<Dependency> detectDependencies() throws DependencyDetectLatestVersionException, DependencyFetchingException, TechnologyExtractDependenciesException {
        List<Dependency> dependencies = new ArrayList<>();
        for (String file : filesPaths) {
            List<Dependency> extractedDependencies = extractDependenciesFromFile(new File(file));
            for (Dependency dependency : extractedDependencies) {
                if (!dependencies.contains(dependency)) {
                    dependencies.add(dependency);
                }
            }
        }
        for (Dependency dependency : dependencies) {
            dependency.detectLatestVersion();
        }
        return dependencies;
    }

    // Private methods
    /**
     * Extracts the dependencies from a file containing a JSON dependency list
     * @param file the file containing the dependency list
     * @return a list of dependencies
     * @throws TechnologyExtractDependenciesException 
     */
    private List<Dependency> extractDependenciesFromFile(File file) throws TechnologyExtractDependenciesException {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            String content = Files.readString(file.toPath());
            dependencies = extractDependencies(content);
        } catch (Exception e) {
            throw new TechnologyExtractDependenciesException("Error while reading file " + file.getAbsolutePath(), e);
        }
        return dependencies;
    }

    /**
     * Extracts the dependencies from a string
     * @param content the string containing the dependencies
     * @return a list of dependencies
     */
    public abstract List<Dependency> extractDependencies(String content) throws TechnologyExtractDependenciesException;

    /**
     * Updates the dependencies in the files given by the filesPaths attribute
     * @param dependencies the dependencies to update
     */
    public abstract void updateDependencies(List<Dependency> dependencies) throws TechnologyUpdateDependenciesException;

    /**
     * Clones the technology to a new instance
     * @return a clone of the technology
     */
    public abstract AbstractTechnology copy();
}
