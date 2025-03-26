package com.info803.dependency_manager_api.infrastructure.utils;

import java.util.List;

public enum TechnologyType {
    JAVA_MAVEN("Java (Maven)", "/project/dependencies/dependency", "pom.xml"),
    JAVA_GRADLE("Java (Gradle)", "dependencies", "build.gradle", "build.gradle.kts"),
    NODE_JS("Node.js", "dependencies", "package.json"),
    PYTHON("Python", null, "requirements.txt", "pyproject.toml"), // Lecture ligne par ligne
    PHP("PHP", "require", "composer.json"),
    DOTNET("C# (.NET)", "ItemGroup/PackageReference", ".csproj"),
    RUST("Rust", "dependencies", "Cargo.toml"),
    GO("Go", "require", "go.mod"),
    RUBY("Ruby", null, "Gemfile"), // Lecture ligne par ligne
    UNKNOWN("Unknown", null);

    private final String name;
    private final String dependencyKey;
    private final String[] files;

    TechnologyType(String name, String dependencyKey, String... files) {
        this.name = name;
        this.dependencyKey = dependencyKey;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public String[] getFiles() {
        return files;
    }
        
}
