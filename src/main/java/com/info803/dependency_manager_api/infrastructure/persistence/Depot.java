package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

// Git import
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.info803.dependency_manager_api.infrastructure.utils.BddEntity;
import com.info803.dependency_manager_api.infrastructure.utils.Technology;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    // Constructors
    public Depot() {}

    public Depot(String name, String url, String token, Long accountId) {
        this.name = name;
        this.url = url;
        this.token = token;
        this.accountId = accountId;
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
        return "depots/" + id;
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

    // Methods
    /**
     * Clones the repository at the given URL and token into a directory at depots/<id>
     * @return a String indicating whether the depot was cloned or not
     */
    public String gitClone() {
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(new File(getPath()))
                .call();
            return "Depot cloned successfully to " + getPath();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Pulls the repository at the given URL and token from the directory at depots/<id>
     * @return a String indicating whether the depot was pulled or not
     * @throws RepositoryNotFoundException 
    */
    public String gitPull() throws RepositoryNotFoundException {
        if (getPath() == null) {
            throw new RepositoryNotFoundException("Git pull : Path is null");
        }
        try (Git git = Git.open(new File(getPath()))) {
            git.pull().call();
            return "Depot pulled successfully to " + getPath();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Lists all files in the cloned repository at depots/<id>
     * @return an array of File objects representing the files in the repository
     * @throws RepositoryNotFoundException if the cloned repository does not exist
     * @throws RuntimeException if any other error occurs
     */
    public List<File> gitCode() throws RepositoryNotFoundException {
        if (getPath() == null) {
            throw new RepositoryNotFoundException("Git code : Path is null");
        }
        try {
            File repoDirectory = new File(getPath());

            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            // Liste tous les fichiers dans le répertoire cloné
            return listDirectoryFiles(repoDirectory);
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
        if (getPath() == null) {
            return "Error deleting depot code: depot code path is null";
        }

        try {
            // Supprime le répertoire cloné
            File repoDirectory = new File(getPath());
            if (!repoDirectory.exists() && !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            deleteDirectoryContent(repoDirectory);
            repoDirectory.delete();
            return "Depot deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Detects the technology used in the cloned repository directory.
     * @return a String representing the technology type detected, or an error message if detection fails.
     * @throws RepositoryNotFoundException if the cloned repository does not exist.
     */

    public String gitCodeTechnology() {
        if (getPath() == null) {
            return "Error getting depot code technology: depot code path is null";
        }

        try {
            // Détecte la technologie utilisée dans le répertoire cloné
            File repoDirectory = new File(getPath());
            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            return Technology.detectTechnologies(getPath()).toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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

    private List<File> listDirectoryFiles(File directory) {
        List<File> fileList = new ArrayList<>();
        
        // Vérifie si le répertoire existe et est un répertoire
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Appel récursif pour les sous-répertoires
                        fileList.addAll(listDirectoryFiles(file));
                    } else {
                        // Ajouter les fichiers au résultat
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }
}
