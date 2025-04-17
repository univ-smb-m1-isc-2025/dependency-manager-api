package com.info803.dependency_manager_api.domain.technology;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;

@ExtendWith(MockitoExtension.class)
public class TechnologyScannerServiceTest {

    @Mock
    private AbstractTechnology technology1;

    @Mock
    private AbstractTechnology technology2;

    private TechnologyScannerService technologyScannerService;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        List<AbstractTechnology> technologies = Arrays.asList(technology1, technology2);
        technologyScannerService = new TechnologyScannerService(technologies);
    }

    @Test
    void detectTechnologies_WhenDirectoryExists_ShouldDetectTechnologies() throws Exception {
        // Arrange
        when(technology1.getFilesNames()).thenReturn(Arrays.asList("file1.txt"));
        when(technology2.getFilesNames()).thenReturn(Arrays.asList("file2.txt"));
        
        File file1 = new File(tempDir, "file1.txt");
        File file2 = new File(tempDir, "file2.txt");
        file1.createNewFile();
        file2.createNewFile();

        when(technology1.copy()).thenReturn(technology1);
        when(technology2.copy()).thenReturn(technology2);

        // Act
        List<AbstractTechnology> result = technologyScannerService.detectTechnologies(tempDir.getAbsolutePath());

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(technology1));
        assertTrue(result.contains(technology2));
    }

    @Test
    void detectTechnologies_WhenDirectoryDoesNotExist_ShouldThrowException() {
        // Arrange
        String nonExistentPath = "/non/existent/path";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            technologyScannerService.detectTechnologies(nonExistentPath)
        );
    }

    @Test
    void detectTechnologies_WhenEmptyDirectory_ShouldReturnEmptyList() {
        // Act
        List<AbstractTechnology> result = technologyScannerService.detectTechnologies(tempDir.getAbsolutePath());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void detectDependencies_WhenTechnologiesExist_ShouldReturnDependencies() throws Exception {
        // Arrange
        List<AbstractTechnology> technologies = Arrays.asList(technology1);
        List<Dependency> dependencies = Arrays.asList(mock(Dependency.class));
        when(technology1.getName()).thenReturn("Tech1");
        when(technology1.detectDependencies()).thenReturn(dependencies);

        // Act
        Map<String, List<Dependency>> result = technologyScannerService.detectDependencies(technologies);

        // Assert
        assertEquals(1, result.size());
        assertEquals(dependencies, result.get("Tech1"));
        verify(technology1).detectDependencies();
    }

    @Test
    void detectDependencies_WhenNoTechnologies_ShouldReturnEmptyMap() throws Exception {
        // Act
        Map<String, List<Dependency>> result = technologyScannerService.detectDependencies(Collections.emptyList());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void detectDependencies_WhenExceptionOccurs_ShouldPropagateException() throws Exception {
        // Arrange
        List<AbstractTechnology> technologies = Arrays.asList(technology1);
        when(technology1.detectDependencies()).thenThrow(new TechnologyExtractDependenciesException("Error"));

        // Act & Assert
        assertThrows(TechnologyExtractDependenciesException.class, () ->
            technologyScannerService.detectDependencies(technologies)
        );
    }
} 