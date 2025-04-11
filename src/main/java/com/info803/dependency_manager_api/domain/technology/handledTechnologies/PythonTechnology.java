package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.PythonDependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class PythonTechnology extends AbstractTechnology {

    public PythonTechnology() {
        super("Python", null, Arrays.asList("requirements.txt"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) {
        List<Dependency> dependencies = new ArrayList<>();

        // Regex pour capter : nom, opérateur, version
        Pattern pattern = Pattern.compile("^\\s*([a-zA-Z0-9_.-]+)\\s*(==|>=|<=|~=|!=|>|<)?\\s*([a-zA-Z0-9_.+-]*)");

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

        return dependencies;
    }

}
