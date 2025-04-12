package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class TerraformTechnology extends AbstractTechnology {
    
    public TerraformTechnology() {
        super("Terraform", "required_providers", Arrays.asList("main.tf"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractDependencies'");
    }

    @Override
    public void updateDependencies(List<Dependency> dependencies) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDependencies'");
    }
    
    @Override
    public AbstractTechnology copy() {
        return new TerraformTechnology();
    }
}
