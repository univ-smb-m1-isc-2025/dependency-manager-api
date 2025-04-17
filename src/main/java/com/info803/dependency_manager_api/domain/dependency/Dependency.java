package com.info803.dependency_manager_api.domain.dependency;

import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyDetectLatestVersionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.dependency.DependencyFetchingException;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public abstract class Dependency {

    // Attributes
    protected String name;
    protected String current;
    protected String latest;

    // Constructors
    protected Dependency(String name, String current) {
        this.name = name;
        this.current = current;
    }

    protected Dependency(String name, String current, String latest) {
        this.name = name;
        this.current = current;
        this.latest = latest;
    }

    public abstract void detectLatestVersion() throws DependencyDetectLatestVersionException, DependencyFetchingException;
}