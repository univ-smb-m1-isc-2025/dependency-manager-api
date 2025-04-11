package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.PHPDependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class PHPTechnology extends AbstractTechnology {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PHPTechnology() {
        super("PHP", "require", Arrays.asList("composer.json"));
    }
   
    /**
     * Extracts the dependencies from a JSON string containing a JSON object with a "dependencies" key.
     * @param content the JSON string
     * @param key the key of the JSON object containing the dependencies
     * @return
     *  a list of dependencies
     */
    @Override
    public List<Dependency> extractDependencies(String content) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(content);
            JsonNode dependenciesNode = rootNode.path(this.getDependencyKey());

            if (dependenciesNode.isObject()) {
                dependenciesNode.fields().forEachRemaining(entry ->
                    dependencies.add(new PHPDependency(entry.getKey(), entry.getValue().asText()))
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing JSON", e);
        }
        return dependencies;
    }
    
}
