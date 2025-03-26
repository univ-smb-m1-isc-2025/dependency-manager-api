package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DependencyDetector {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DependencyDetector() {}

    /**
     * Detects the dependencies used in the code of a depot
     * @param technologies map of technologies used in the depot and their associated files
     * @return a map of technologies and their associated dependencies
     */
    public static Map<TechnologyType, Map<String, String>> detectDependencies(Map<TechnologyType, List<String>> technologies) {
        Map<TechnologyType, Map<String, String>> dependenciesMap = new HashMap<>();

        for (Map.Entry<TechnologyType, List<String>> entry : technologies.entrySet()) {
            TechnologyType technology = entry.getKey();
            List<String> files = entry.getValue();
            Map<String, String> dependencies = new HashMap<>();

            for (String filePath : files) {
                File file = new File(filePath);
                if (file.exists()) {
                    dependencies.putAll(extractDependenciesFromFile(technology, file));
                }
            }
            dependenciesMap.put(technology, dependencies);
        }
        return dependenciesMap;
    }


    /**
     * Extracts the dependencies from a file containing a JSON dependency list
     * @param technology the technology that is associated with the file
     * @param file the file containing the dependency list
     * @return a list of dependencies
     * @throws RuntimeException if there is an error while reading the file
     */
    private static Map<String, String> extractDependenciesFromFile(TechnologyType technology, File file) {
        Map<String, String> dependencies = new HashMap<>();
        try {
            String content = Files.readString(file.toPath());
            dependencies = extractJsonDependencies(content, technology.getDependencyKey());
        } catch (Exception e) {
            throw new RuntimeException("Error while reading file " + file.getAbsolutePath(), e);
        }
        return dependencies;
    }


    /**
     * Extracts the dependencies from a JSON string containing a JSON object with a "dependencies" key.
     * @param content the JSON string
     * @param key the key of the JSON object containing the dependencies
     * @return a list of dependencies
     */
    private static Map<String, String> extractJsonDependencies(String content, String key) {
        Map<String, String> dependencies = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(content);
            JsonNode dependenciesNode = rootNode.path(key);

            if (dependenciesNode.isObject()) {
                dependenciesNode.fields().forEachRemaining(entry ->
                        dependencies.put(entry.getKey(), entry.getValue().asText())
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing JSON", e);
        }
        return dependencies;
    }
}
