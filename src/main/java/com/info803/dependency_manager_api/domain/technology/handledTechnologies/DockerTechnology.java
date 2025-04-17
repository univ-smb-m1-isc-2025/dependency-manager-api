package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class DockerTechnology extends AbstractTechnology {
    
    public DockerTechnology() {
        super("Docker", "services", Arrays.asList("docker-compose.yml"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) throws TechnologyExtractDependenciesException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractDependencies'");
    }

    @Override
    public void updateDependencies(List<Dependency> dependencies) throws TechnologyUpdateDependenciesException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDependencies'");
    }

    @Override
    public AbstractTechnology copy() {
        return new DockerTechnology();
    }
    
}
