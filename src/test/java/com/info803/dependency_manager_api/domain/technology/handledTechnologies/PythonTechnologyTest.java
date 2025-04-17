package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.PythonDependency;

public class PythonTechnologyTest {

    private PythonTechnology pythonTechnology;

    @BeforeEach
    void setUp() {
        pythonTechnology = new PythonTechnology();
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertEquals("Python", pythonTechnology.getName());
        assertNull(pythonTechnology.getDependencyKey());
        assertEquals(1, pythonTechnology.getFilesNames().size());
        assertTrue(pythonTechnology.getFilesNames().contains("requirements.txt"));
    }

    @Test
    void extractDependencies_WithValidRequirementsTxt_ShouldReturnDependencies() throws TechnologyExtractDependenciesException {
        // Arrange
        String requirementsTxt = """
            django==4.0.0
            requests>=2.25.0
            numpy
            # This is a comment
            flask~=2.0.0
            """;

        // Act
        List<Dependency> dependencies = pythonTechnology.extractDependencies(requirementsTxt);

        // Assert
        assertEquals(4, dependencies.size());
        
        PythonDependency django = (PythonDependency) dependencies.get(0);
        assertEquals("django", django.getName());
        assertEquals("4.0.0", django.getCurrent());

        PythonDependency requests = (PythonDependency) dependencies.get(1);
        assertEquals("requests", requests.getName());
        assertEquals("2.25.0", requests.getCurrent());

        PythonDependency numpy = (PythonDependency) dependencies.get(2);
        assertEquals("numpy", numpy.getName());
        assertNull(numpy.getCurrent());

        PythonDependency flask = (PythonDependency) dependencies.get(3);
        assertEquals("flask", flask.getName());
        assertEquals("2.0.0", flask.getCurrent());
    }

    @Test
    void extractDependencies_WithEmptyContent_ShouldReturnEmptyList() throws TechnologyExtractDependenciesException {
        // Arrange
        String emptyContent = "";

        // Act
        List<Dependency> dependencies = pythonTechnology.extractDependencies(emptyContent);

        // Assert
        assertTrue(dependencies.isEmpty());
    }

    @Test
    void copy_ShouldReturnNewInstance() {
        // Act
        PythonTechnology copy = (PythonTechnology) pythonTechnology.copy();

        // Assert
        assertNotSame(pythonTechnology, copy);
        assertEquals(pythonTechnology.getName(), copy.getName());
        assertEquals(pythonTechnology.getDependencyKey(), copy.getDependencyKey());
        assertEquals(pythonTechnology.getFilesNames(), copy.getFilesNames());
    }
} 