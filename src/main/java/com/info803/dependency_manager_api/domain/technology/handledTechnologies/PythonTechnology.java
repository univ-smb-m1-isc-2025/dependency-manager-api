package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.PythonDependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class PythonTechnology extends AbstractTechnology {

    public PythonTechnology() {
        super("Python", null, Arrays.asList("requirements.txt"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) throws TechnologyExtractDependenciesException {
        List<Dependency> dependencies = new ArrayList<>();

        // Regex pour capter : nom, opérateur, version
        Pattern pattern = Pattern.compile("^\\s*([a-zA-Z0-9_.-]+)\\s*(==|>=|<=|~=|!=|>|<)?\\s*([a-zA-Z0-9_.+-]*)");

        try  {

            String[] lines = content.split("\n");
            for (String line : lines) {
                // Ignorer les commentaires et lignes vides
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("-r") || line.startsWith("--")) {
                    continue;
                }

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String name = matcher.group(1);
                    String versionOp = matcher.group(2);
                    String version = matcher.group(3);

                    // Si pas d'opérateur/version, on met version à null ou vide
                    String versionStr = (versionOp != null && version != null && !version.isEmpty()) 
                        ? version 
                        : null;

                    dependencies.add(new PythonDependency(name, versionStr, versionOp));
                }
            }
            
        } catch (Exception e) {
            throw new TechnologyExtractDependenciesException("Error extracting dependencies : " + e.getMessage(), e);
        }
        return dependencies;
    }

    @Override
    public void updateDependencies(List<Dependency> dependencies) throws TechnologyUpdateDependenciesException {
        // Steps :
        // 1. For each file in filesPaths, read the file
        // 2. For each dependency in dependencies, check if it is in the file
        // 3. If it is, replace the dependency with the new version (if current is null don't replace)
        // 4. Write the file
        try {
            for (String file : filesPaths) {
                String content = FileUtils.readFileToString(new File(file), "UTF-8");
                
            for (Dependency dependency : dependencies) {
                if (dependency.getCurrent() != null) {
                    content = content.replace(dependency.getCurrent(), dependency.getLatest());
                }
            }
            
                FileUtils.writeStringToFile(new File(file), content, "UTF-8");
            }
        } catch (Exception e) {
            throw new TechnologyUpdateDependenciesException("Error updating dependencies : " + e.getMessage(), e);
        }
    }

    @Override
    public AbstractTechnology copy() {
        return new PythonTechnology();
    }

}
