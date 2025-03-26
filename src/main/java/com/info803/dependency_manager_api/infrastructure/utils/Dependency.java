package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dependency {

    private Dependency() {}

    public static Map<TechnologyType, List<String>> detectDependencies(Map<TechnologyType, List<String>> technologies) {
        Map<TechnologyType, List<String>> dependenciesMap = new HashMap<>();

        for (Map.Entry<TechnologyType, List<String>> entry : technologies.entrySet()) {
            TechnologyType technology = entry.getKey();
            List<String> files = entry.getValue();
            List<String> dependencies = new ArrayList<>();

            for (String filePath : files) {
                File file = new File(filePath);
                if (file.exists()) {
                    dependencies.addAll(extractDependenciesFromFile(technology, file));
                }
            }
            dependenciesMap.put(technology, dependencies);
        }
        return dependenciesMap;
    }


    private static List<String> extractDependenciesFromFile(TechnologyType technology, File file) {
        List<String> dependencies = new ArrayList<>();
        try {
            String content = Files.readString(file.toPath());
            dependencies = extractJsonDependencies(content, technology.getDependencyKey());
        } catch (Exception e) {
            throw new RuntimeException("Error while reading file " + file.getAbsolutePath(), e);
        }
        return dependencies;
    }


    private static List<String> extractJsonDependencies(String content, String key) {
        List<String> dependencies = new ArrayList<>();
        int index = content.indexOf(key);
        if (index != -1) {
            int start = content.indexOf("{", index);
            int end = content.indexOf("}", start);
            if (start != -1 && end != -1) {
                String dependenciesBlock = content.substring(start + 1, end);
                dependencies = Arrays.stream(dependenciesBlock.split(",")).map(String::trim).collect(Collectors.toList());
            }
        }
        return dependencies;
    }
}
