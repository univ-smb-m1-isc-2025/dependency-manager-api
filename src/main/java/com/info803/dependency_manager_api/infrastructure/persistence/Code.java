package com.info803.dependency_manager_api.infrastructure.persistence;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Code extends BddEntity {
    
    @Id
    @GeneratedValue
    private Long id;

    private String path;
    private Long depotId;

    // Constructors
    public Code() {}

    public Code(String path, Long depotId) {
        this.path = path;
        this.depotId = depotId;
    }

    // Getters
    public String getPath() {
        return path;
    }

    public Long getDepotId() {
        return depotId;
    }

    // Setters
    public void setPath(String path) {
        this.path = path;
    } 

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    // Methods
    public String gitClone(String url, String token) {
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(new File("depots/" + depotId))
                .call();
            return "Depot cloned successfully to depots/" + depotId;
        } catch (Exception e) {
            return "Error cloning depot: " + e.getMessage();
        }
    }

    public String gitPull() {
        try (Git git = Git.open(new File("depots/" + depotId))) {
            git.pull().call();
            return "Depot pulled successfully to depots/" + depotId;
        } catch (Exception e) {
            return "Error pulling depot: " + e.getMessage();
        }
    }
    
    @Transient
    public File[] gitCode() {
        try {
            File repoDirectory = new File("depots/" + depotId);

            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            // Liste tous les fichiers dans le répertoire cloné
            return repoDirectory.listFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String gitDelete() {
        try {
            // Supprime le répertoire cloné
            File repoDirectory = new File("depots/" + depotId);
            if (!repoDirectory.exists() && !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            deleteDirectoryContent(repoDirectory);
            repoDirectory.delete();
            return "Depot deleted successfully";
        } catch (Exception e) {
            return "Error deleting depot: " + e.getMessage();
        }
    }

    public String listDependecies() {
        return "";
    }

    // Private methods
    private void deleteDirectoryContent(File directory) {
        // Recursively delete all files and subdirectories in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryContent(file);
                }
                file.delete(); 
            }
        }
    }
}
