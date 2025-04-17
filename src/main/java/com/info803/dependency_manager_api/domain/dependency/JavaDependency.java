package com.info803.dependency_manager_api.domain.dependency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyFetchingException;

import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JavaDependency extends Dependency {

    private String groupId;

    public JavaDependency(String name, String version, String groupId) {
        super(name, version);
        this.groupId = groupId;
    }

    @Override
    public void detectLatestVersion() throws DependencyDetectLatestVersionException, DependencyFetchingException {
        try {
            String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + name + "&rows=1&wt=json";
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) {
                throw new DependencyFetchingException("Error getting response from " + url + ". Response is null.");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            this.latest = root.path("response").path("docs").get(0).path("latestVersion").asText();

        } catch (Exception e) {
            throw new DependencyDetectLatestVersionException("Error detecting latest version for " + this.name + ": " + e.getMessage(), e);
        }
    }
}
