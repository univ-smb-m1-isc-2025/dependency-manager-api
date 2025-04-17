package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.JavaDependency;

public class JavaTechnologyTest {

    private JavaTechnology javaTechnology;

    @BeforeEach
    void setUp() {
        javaTechnology = new JavaTechnology();
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertEquals("Java", javaTechnology.getName());
        assertEquals("dependencies", javaTechnology.getDependencyKey());
        assertEquals(2, javaTechnology.getFilesNames().size());
        assertTrue(javaTechnology.getFilesNames().contains("pom.xml"));
        assertTrue(javaTechnology.getFilesNames().contains("build.gradle"));
    }

    @Test
    void extractDependencies_WithValidPomXml_ShouldReturnDependencies() throws TechnologyExtractDependenciesException {
        // Arrange
        String pomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework</groupId>
                        <artifactId>spring-core</artifactId>
                        <version>5.3.20</version>
                    </dependency>
                    <dependency>
                        <groupId>org.springframework</groupId>
                        <artifactId>spring-web</artifactId>
                        <version>5.3.20</version>
                    </dependency>
                </dependencies>
            </project>
            """;

        // Act
        List<Dependency> dependencies = javaTechnology.extractDependencies(pomXml);

        // Assert
        assertEquals(2, dependencies.size());
        
        JavaDependency springCore = (JavaDependency) dependencies.get(0);
        assertEquals("spring-core", springCore.getName());
        assertEquals("5.3.20", springCore.getCurrent());
        assertEquals("org.springframework", springCore.getGroupId());

        JavaDependency springWeb = (JavaDependency) dependencies.get(1);
        assertEquals("spring-web", springWeb.getName());
        assertEquals("5.3.20", springWeb.getCurrent());
        assertEquals("org.springframework", springWeb.getGroupId());
    }

    @Test
    void extractDependencies_WithInvalidXml_ShouldThrowException() {
        // Arrange
        String invalidXml = "invalid xml content";

        // Act & Assert
        assertThrows(TechnologyExtractDependenciesException.class, () -> {
            javaTechnology.extractDependencies(invalidXml);
        });
    }

    @Test
    void copy_ShouldReturnNewInstance() {
        // Act
        JavaTechnology copy = (JavaTechnology) javaTechnology.copy();

        // Assert
        assertNotSame(javaTechnology, copy);
        assertEquals(javaTechnology.getName(), copy.getName());
        assertEquals(javaTechnology.getDependencyKey(), copy.getDependencyKey());
        assertEquals(javaTechnology.getFilesNames(), copy.getFilesNames());
    }
} 