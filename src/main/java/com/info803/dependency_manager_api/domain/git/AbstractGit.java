package com.info803.dependency_manager_api.domain.git;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;

public abstract class AbstractGit {

    protected AbstractGit() {}

    public abstract String gitPullRequest(Depot depot);

    /**
     * Clones the repository at the given URL and token into a directory at depots/<id>
     * @return a String indicating whether the depot was cloned or not
     */
    public String gitClone(Depot depot) {
        String url = depot.getUrl();
        String path = depot.getPath();
        String token = depot.getToken();
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .setDirectory(new File(path))
                .call();
            return "Depot cloned successfully to " + path;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Pulls the repository at the given URL and token from the directory at depots/<id>
     * @return a String indicating whether the depot was pulled or not
     * @throws RepositoryNotFoundException 
    */
    public String gitPull(Depot depot) throws RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new RepositoryNotFoundException("Git pull : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.pull().call();
            return "Depot pulled successfully to " + path;
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
    public List<File> gitCode(Depot depot) {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Git code : Path is null");
        }
        try {
            File repoDirectory = new File(path);

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
    public String gitDelete(Depot depot) {
        String path = depot.getPath();
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
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Detects the technology used in the cloned repository directory.
     * @return a String representing the technology type detected, or an error message if detection fails.
     * @throws RepositoryNotFoundException if the cloned repository does not exist.
     */

    public List<AbstractTechnology> gitCodeTechnologies(Depot depot) {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Error getting depot code technology: depot code path is null");
        }

        try {
            // Détecte la technologie utilisée dans le répertoire cloné
            File repoDirectory = new File(path);
            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            return TechnologyScannerService.detectTechnologies(path);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Detects the dependencies used in the cloned repository directory.
     * @return a String representing the dependency type detected, or an error message if detection fails.
     * @throws RepositoryNotFoundException if the cloned repository does not exist.
     */
    public Map<String, List<Dependency>> gitCodeDependencies(Depot depot) {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Error getting depot code dependency: depot code path is null");
        }

        try {
            // Get technologies used in the cloned repository directory
            List<AbstractTechnology> technologies = gitCodeTechnologies(depot);
            // Get dependencies used in the cloned repository directory
            return TechnologyScannerService.detectDependencies(technologies);
            
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Stages changes in the working directory that match the given file pattern.
     * @param depot The Depot object representing the repository.
     * @param filepattern The pattern of files to add (e.g., "." for all changes).
     * @return A String indicating the result of the add operation.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitAdd(Depot depot, String filepattern) throws RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Git add : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.add().addFilepattern(filepattern).call();
            return "Changes added successfully for pattern: " + filepattern;
        } catch (RepositoryNotFoundException e) {
             throw e; // Re-throw specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error during git add: " + e.getMessage(), e);
        }
    }

     /**
     * Commits the staged changes to the local repository.
     * @param message The commit message.
     * @return A String indicating the result of the commit operation.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitCommit(Depot depot, String message) throws RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Git commit : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.commit().setMessage(message).call();
            return "Changes committed successfully with message: " + message;
        } catch (RepositoryNotFoundException e) {
             throw e; // Re-throw specific exception
        } catch (Exception e) {
            // Handle cases like no changes to commit, etc.
            throw new RuntimeException("Error during git commit: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the current branch of the local repository.
     * @return A String representing the current branch.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitGetBranch(Depot depot) {

        String path = depot.getPath();

        if (path == null) {
            throw new RuntimeException("Git branch : Path is null");
        }

        try (Git git = Git.open(new File(path))) {
            return git.getRepository().getBranch();
        } catch (Exception e) {
            throw new RuntimeException("Error during git branch: " + e.getMessage(), e);
        }
    }

    /**
     * Pushes committed changes from the local repository to the remote repository.
     * @return A String indicating the result of the push operation.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitPush(Depot depot) throws RepositoryNotFoundException {
        String path = depot.getPath();
        String token = depot.getToken();
        if (path == null) {
            throw new RuntimeException("Git push : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.push()
               .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
               .call();
            return "Changes pushed successfully to remote repository.";
        } catch (RepositoryNotFoundException e) {
             throw e; // Re-throw specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error during git push: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new branch in the local repository and switches to it.
     * @return A String representing the name of the new branch.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitCreateBranch(Depot depot) {
        String path = depot.getPath();
        String branch = depot.getBranch();
        String branchName = generateBranchName(depot);

        // if (path == null) {
        //     throw new RuntimeException("Git create branch : Path is null");
        // }
        
        // try (Git git = Git.open(new File(path))) {
        //     git.checkout()
        //         .setCreateBranch(true)
        //         .setName(branchName)
        //         .setStartPoint("origin/" + branch)
        //         .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
        //         .call();
        // } catch (Exception e) {
        //     throw new RuntimeException("Error during git create branch: " + e.getMessage(), e);
        // }
        return branchName;
    }

    /**
     * Switches to a branch in the local repository.
     * @param branchName The name of the branch to checkout.
     * @return A String indicating the result of the checkout operation.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitCheckoutBranch(Depot depot, String branchName) {
        String path = depot.getPath();
        if (path == null) {
            throw new RuntimeException("Git checkout branch : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.checkout().setName(branchName).call();
        } catch (Exception e) {
            throw new RuntimeException("Error during git checkout branch: " + e.getMessage(), e);
        }
        return "Checked out branch: " + branchName; 
    }

    public String generateBranchName(Depot depot) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        
        return "dependency-manager-fix-" + dateString;
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
