package com.info803.dependency_manager_api.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

// Git import
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.lang.reflect.Field;

@Entity
public class Depot {
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

    // Setters
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
    public String gitClone() {
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(new File("depots/" + id))
                .call();
            return "Depot cloned successfully to depots/" + id;
        } catch (Exception e) {
            return "Error cloning depot: " + e.getMessage();
        }
    }

    public String gitPull() {
        try (Git git = Git.open(new File("depots/" + id))) {
            git.pull().call();
            return "Depot pulled successfully to depots/" + id;
        } catch (Exception e) {
            return "Error pulling depot: " + e.getMessage();
        }
    }
    
    public File[] gitCode() {
        try {
            File repoDirectory = new File("depots/" + id);

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
            File repoDirectory = new File("depots/" + id);
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

    public void updateFrom(Depot depot) {
        if (depot == null) {
            throw new IllegalArgumentException("Depot is null");
        }

        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object newValue = field.get(depot);
                if (newValue != null && !java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    field.set(this, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field : " + field.getName(), e);
            }
        }
    }
}
