package com.info803.dependency_manager_api.domain.git;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitActionException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitAddException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitBranchException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCheckoutException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCloneException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCodeException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCommitException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNoChangesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPushException;
import com.info803.dependency_manager_api.config.EncryptionService;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;
import com.info803.dependency_manager_api.domain.technology.TechnologyScannerService;

import org.springframework.stereotype.Service;

@Service
public abstract class AbstractGit {

    protected String gitApiUrl;

    protected final EncryptionService encryptionService;
    protected final TechnologyScannerService technologyScannerService;


    protected AbstractGit(String gitApiUrl, EncryptionService encryptionService, TechnologyScannerService technologyScannerService) {
        this.gitApiUrl = gitApiUrl;
        this.encryptionService = encryptionService;
        this.technologyScannerService = technologyScannerService;
    }

    public abstract String getName();

    public abstract String getIconUrl();

    public abstract String gitCreatePullRequest(Depot depot, String branchName) throws GitPullRequestException;

    public abstract boolean isGit(String url);

    public abstract String extractOwner(String url);

    public abstract String extractRepoName(String url);

    public <T> T executeGitAction(Depot depot, GitFunction<Depot, T> gitAction) throws GitActionException {
        try {
            depot.setToken(encryptionService.decrypt(depot.getToken()));
            T result = gitAction.apply(depot);
            depot.setToken(encryptionService.encrypt(depot.getToken()));
            return result;
        } catch (Exception e) {
            throw new GitActionException("Error during git action: " + e.getMessage(), e);
        }
    }
    

    /**
     * Creates a new branch, adds all changes, commits them, and pushes them to the remote repository.
     * @param depot The Depot object representing the repository.
     * @return A String indicating the result of the pull request operation.
     * @throws GitPullRequestException if there is an error during the pull request operation.
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitPullRequest(Depot depot) throws GitPullRequestException, RepositoryNotFoundException {
        try {
            // 1- Create a new branch from the master branch and commit the changes to it.
            // Create a new branch
            String newBranchName = gitCreateBranch(depot);

            // Add the changes to the branch
            gitAdd(depot, ".");
            // Commit the changes
            gitCommit(depot, "Commit changes");

            // Push the changes to the branch
            gitPush(depot);     
            
            // 2- Create a pull request between the master branch and the new branch.
            return gitCreatePullRequest(depot, newBranchName);
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitPullRequestException("Error during git pull request: " + e.getMessage(), e);
        }
    }

    /**
     * Clones the repository at the given URL and token into a directory at depots/<id>
     * @return a String indicating whether the depot was cloned or not
     * @throws GitCloneException
     */
    public String gitClone(Depot depot) throws GitCloneException{
        String url = depot.getUrl();
        String path = depot.getPath();
        String username = depot.getUsername();
        String token = depot.getToken();
        try {
            // Clone the repository
            Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                .setDirectory(new File(path))
                .call();
            return "Depot cloned successfully to " + path;
        } catch (Exception e) {
            throw new GitCloneException("Error during git clone: " + e.getMessage(), e);
        }
    }

    /**
     * Pulls the repository at the given URL and token from the directory at depots/<id>
     * @return a String indicating whether the depot was pulled or not
     * @throws GitPullException 
     * @throws IllegalArgumentException 
     * @throws RepositoryNotFoundException 
    */
    public String gitPull(Depot depot) throws GitPullException, IllegalArgumentException, RepositoryNotFoundException {
        String username = depot.getUsername();
        String token = depot.getToken();
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git pull : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.pull()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                .call();
            return "Depot pulled successfully to " + path;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitPullException("Error during git pull: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lists all files in the cloned repository at depots/<id>
     * @return an array of File objects representing the files in the repository
     * @throws GitCodeException
          * @throws RepositoryNotFoundException 
          * @throws IllegalArgumentException
          */
         public List<File> gitCode(Depot depot) throws GitCodeException, RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git code : Path is null");
        }
        try {
            File repoDirectory = new File(path);

            if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
                throw new RepositoryNotFoundException("Cloned repository not found.");
            }
            // Liste tous les fichiers dans le répertoire cloné
            return listDirectoryFiles(repoDirectory);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitCodeException("Error during git code: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the cloned repository at depots/<id>
     * @return a String indicating whether the depot was deleted or not
     * @throws GitDeleteException
     *  @throws RepositoryNotFoundException 
     * @throws IllegalArgumentException
    */
    public String gitDelete(Depot depot) throws GitDeleteException, RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git delete : Path is null");
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
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitDeleteException("Error during git delete: " + e.getMessage(), e);
        }
    }

    /**
     * Detects the technology used in the cloned repository directory.
     * @return a String representing the technology type detected, or an error message if detection fails.
     * @throws IllegalArgumentException
     * @throws GitCodeException
     */
    public List<AbstractTechnology> gitCodeTechnologies(Depot depot) throws GitCodeException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Error getting depot code technology: depot code path is null");
        }

        try {
            return technologyScannerService.detectTechnologies(path);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new GitCodeException("Error getting code technologies: " + e.getMessage(), e);
        }
    }

    /**
     * Detects the dependencies used in the cloned repository directory.
     * @return a String representing the dependency type detected, or an error message if detection fails.
     * @throws IllegalArgumentException
     * @throws GitCodeException
     */
    public Map<String, List<Dependency>> gitCodeDependencies(Depot depot) throws GitCodeException{
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Error getting depot code dependency: depot code path is null");
        }

        try {
            // Get technologies used in the cloned repository directory
            List<AbstractTechnology> technologies = gitCodeTechnologies(depot);
            // Get dependencies used in the cloned repository directory
            return technologyScannerService.detectDependencies(technologies);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new GitCodeException("Error getting code dependencies: " + e.getMessage(), e);
        }
    }

    /**
     * Stages changes in the working directory that match the given file pattern.
     * @param depot The Depot object representing the repository.
     * @param filepattern The pattern of files to add (e.g., "." for all changes).
     * @return A String indicating the result of the add operation.
     * @throws GitAddException
     * @throws RepositoryNotFoundException 
     * @throws GitNoChangesException 
    */
    public String gitAdd(Depot depot, String filepattern) throws GitAddException, RepositoryNotFoundException, GitNoChangesException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git add : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            if (git.status().call().hasUncommittedChanges()) {
                git.add().addFilepattern(filepattern).call();
                return "Changes added successfully for pattern: " + filepattern;
            } else {
                throw new GitNoChangesException("No changes to add for pattern: " + filepattern);
            }
        } catch (GitNoChangesException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
             throw e;
        } catch (Exception e) {
            throw new GitAddException("Error during git add: " + e.getMessage(), e);
        }
    }

     /**
     * Commits the staged changes to the local repository.
     * @param message The commit message.
     * @return A String indicating the result of the commit operation.
     * @throws GitCommitException
     * @throws RepositoryNotFoundException
     * @throws IllegalArgumentException
     */
    public String gitCommit(Depot depot, String message) throws GitCommitException, RepositoryNotFoundException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git commit : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.commit().setMessage(message).call();
            return "Changes committed successfully with message: " + message;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
             throw e; // Re-throw specific exception
        } catch (Exception e) {
            throw new GitCommitException("Error during git commit: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the current branch of the local repository.
     * @return A String representing the current branch.
     * @throws GitBranchException
     * @throws IllegalArgumentException
     * @throws RepositoryNotFoundException
     */
    public String gitGetBranch(Depot depot) throws GitBranchException, RepositoryNotFoundException{

        String path = depot.getPath();

        if (path == null) {
            throw new IllegalArgumentException("Git branch : Path is null");
        }

        try (Git git = Git.open(new File(path))) {
            return git.getRepository().getBranch();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitBranchException("Error during git branch: " + e.getMessage(), e);
        }
    }

    /**
     * Pushes committed changes from the local repository to the remote repository.
     * @return A String indicating the result of the push operation.
     * @throws GitPushException
     * @throws IllegalArgumentException
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitPush(Depot depot) throws GitPushException, RepositoryNotFoundException {
        String path = depot.getPath();
        String username = depot.getUsername();
        String token = depot.getToken();
        if (path == null) {
            throw new IllegalArgumentException("Git push : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.push()
               .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
               .call();
            return "Changes pushed successfully to remote repository.";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
             throw e; 
        } catch (Exception e) {
            throw new GitPushException("Error during git push: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new branch in the local repository and switches to it.
     * @return A String representing the name of the new branch.
     * @throws GitBranchException
     * @throws IllegalArgumentException
     * @throws RepositoryNotFoundException 
     */
    public String gitCreateBranch(Depot depot) throws GitBranchException, RepositoryNotFoundException {
        String path = depot.getPath();
        String branch = depot.getBranch();
        String branchName = generateBranchName();

        if (path == null) {
            throw new IllegalArgumentException("Git create branch : Path is null");
        }
        
        try (Git git = Git.open(new File(path))) {
            git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint("origin/" + branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .call();
                return branchName;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitBranchException("Error during git create branch: " + e.getMessage(), e);
        }
    }

    /**
     * Switches to a branch in the local repository.
     * @param depot The Depot object representing the repository.
     * @param branchName The name of the branch to checkout.
     * @return A String indicating the result of the checkout operation.
     * @throws GitCheckoutException
     * @throws IllegalArgumentException
     * @throws RepositoryNotFoundException if the repository cannot be found.
     */
    public String gitCheckoutBranch(Depot depot, String branchName) throws GitCheckoutException, RepositoryNotFoundException  {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git checkout branch : Path is null");
        }
        try (Git git = Git.open(new File(path))) {
            git.checkout().setName(branchName).call();
            return "Checked out branch: " + branchName; 
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RepositoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GitCheckoutException("Error during git checkout branch: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the dependencies in the local repository.
     * @param depot The Depot object representing the repository.
     * @return A String indicating the result of the update operation.
     */
    public String gitCodeDependenciesUpdate(Depot depot) throws GitCodeException {
        String path = depot.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Git code dependencies update : Path is null");
        }

        try {
            // Get technologies used in the cloned repository directory
            List<AbstractTechnology> technologies = gitCodeTechnologies(depot);
            // Get dependencies used in the cloned repository directory
            Map<String, List<Dependency>> dependencies = technologyScannerService.detectDependencies(technologies);
            // Update the dependencies
            for (AbstractTechnology technology : technologies) {
                technology.updateDependencies(dependencies.get(technology.getName()));
            }
            return "Dependencies updated successfully";
        } catch (IllegalArgumentException e) {
            throw e;    
        } catch (Exception e) {
            throw new GitCodeException("Error during git code dependencies update on local repository: " + e.getMessage(), e);
        }
    }

    public String generateBranchName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
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
