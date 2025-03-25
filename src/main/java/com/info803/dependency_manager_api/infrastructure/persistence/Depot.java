package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;

// Git import
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.info803.dependency_manager_api.infrastructure.utils.BddEntity;
import com.info803.dependency_manager_api.infrastructure.utils.Technology;

import java.io.File;

@Entity
public class Depot extends BddEntity{
    
    // Attributes
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String url;
    private String token;
    private Long accountId;
    private String path;

    // Constructors
    public Depot() {}

    public Depot(String name, String url, String token, Long accountId) {
        this.name = name;
        this.url = url;
        this.token = token;
        this.accountId = accountId;
        this.path = null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {    
        return token;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getPath() {
        return path;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setToken(String token) {    
        this.token = token;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Methods
    /**
     * Clones the repository at the given URL and token into a directory at depots/<id>
     * @return a String indicating whether the depot was cloned or not
     */
    public String gitClone() {
        if (path == null) {
            this.path = "depots/" + this.id;
        }
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(new File(path))
                .call();
            return "Depot cloned successfully to " + path;
        } catch (Exception e) {
            return "Error cloning depot: " + e.getMessage();
        }
    }

    /**
     * Pulls the repository at the given URL and token from the directory at depots/<id>
     * @return a String indicating whether the depot was pulled or not
     */
    public String gitPull() {
        if (path == null) {
            return "Error pulling depot: depot code path is null";
        }
        try (Git git = Git.open(new File(path))) {
            git.pull().call();
            return "Depot pulled successfully to " + path;
        } catch (Exception e) {
            return "Error pulling depot: " + e.getMessage();
        }
    }
    
    /**
     * Lists all files in the cloned repository at depots/<id>
     * @return an array of File objects representing the files in the repository
     * @throws RepositoryNotFoundException if the cloned repository does not exist
     * @throws RuntimeException if any other error occurs
     */
    public File[] gitCode() throws RepositoryNotFoundException {
        if (path == null) {
            throw new RepositoryNotFoundException("Git code : Path is null");
        }

        try {
            File repoDirectory = new File(path);

            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            // Liste tous les fichiers dans le répertoire cloné
            return repoDirectory.listFiles();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deletes the cloned repository at depots/<id>
     * @return a String indicating whether the depot was deleted or not
     * @throws RepositoryNotFoundException if the cloned repository does not exist
     * @throws RuntimeException if any other error occurs
     */
    public String gitDelete() {
        if (path == null) {
            return "Error deleting depot code: depot code path is null";
        }

        try {
            // Supprime le répertoire cloné
            File repoDirectory = new File(path);
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

    /**
     * Detects the technology used in the cloned repository directory.
     * @return a String representing the technology type detected, or an error message if detection fails.
     * @throws RepositoryNotFoundException if the cloned repository does not exist.
     */

    public String gitCodeTechnology() {
        if (path == null) {
            return "Error getting depot code technology: depot code path is null";
        }

        try {
            // Détecte la technologie utilisée dans le répertoire cloné
            File repoDirectory = new File(path);
            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            return Technology.detectTechnology(path).toString();
        } catch (Exception e) {
            return "Error detecting technology: " + e.getMessage();
        }
    }

    /**
     * Returns a string listing all dependencies for the given depot.
     * @return a string listing all dependencies for the given depot
     */
    public String listDependecies() {
        // TODO
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
