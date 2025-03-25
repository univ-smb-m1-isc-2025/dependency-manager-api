package com.info803.dependency_manager_api.infrastructure.utils;

public enum TechnologyType {
    JAVA_MAVEN("Java (Maven)", "pom.xml"),
    JAVA_GRADLE("Java (Gradle)", "build.gradle", "build.gradle.kts"),
    NODE_JS("Node.js", "package.json"),
    PYTHON("Python", "requirements.txt", "pyproject.toml"),
    PHP("PHP", "composer.json"),
    DOTNET("C# (.NET)", ".csproj"),
    RUST("Rust", "Cargo.toml"),
    GO("Go", "go.mod"),
    RUBY("Ruby", "Gemfile"),
    UNKNOWN("Unknown");

    private final String name;
    private final String[] files;

    TechnologyType(String name, String... files) {
        this.name = name;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public String[] getFiles() {
        return files;
    }
}
