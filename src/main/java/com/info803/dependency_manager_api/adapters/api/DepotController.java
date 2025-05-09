package com.info803.dependency_manager_api.adapters.api;

import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotCreationException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.depot.DepotUpdateException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitBranchException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCloneException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitCodeException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitDeleteException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitNotFoundException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.git.GitPullRequestException;
import com.info803.dependency_manager_api.adapters.api.response.ApiResponse;
import com.info803.dependency_manager_api.adapters.api.response.ResponseUtil;
import com.info803.dependency_manager_api.application.DepotService;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import java.io.File;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/depots")
public class DepotController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }

    /**
     * Retrives a list aff all depots
     * 
     * @return a list of all Depot objects
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Depot>>> depotList() {
        logger.info("depotsList");

        List<Depot> depots = depotService.depotList();

        ApiResponse<List<Depot>> response = ResponseUtil.success("Depots retrieved", depots);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
    /**
     * Retrives a single depot by its id
     * 
     * @param id the unique identifier of the depot to retrieve
     * @return the Depot object with the given id
     * @throws DepotNotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Depot>>> depot(@PathVariable Long id) throws DepotNotFoundException {
        logger.info("depot");
        
        Optional<Depot> depot = depotService.depot(id);

        ApiResponse<Optional<Depot>> response = ResponseUtil.success("Depot retrieved", depot);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Creates a new depot with the given depot information
     * 
     * @param depot the Depot object containing the name and description
     * @return a String indicating whether the depot was created or not
     * @throws AccountNotFoundException
     * @throws DepotCreationException
     * @throws GitNotFoundException
     * @throws GitBranchException 
     * @throws GitCloneException
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Depot>> create(@RequestBody Depot depot) throws AccountNotFoundException, DepotCreationException, GitNotFoundException, GitBranchException, GitCloneException {
        logger.info("create");

        Depot newDepot = depotService.create(depot);

        ApiResponse<Depot> response = ResponseUtil.success("Depot created", newDepot);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Deletes a depot by its id
     * 
     * @param id the unique identifier of the depot to delete
     * @return a String indicating whether the depot was deleted or not
     * @throws DepotNotFoundException
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws DepotNotFoundException {
        logger.info("delete");

        depotService.delete(id);

        ApiResponse<String> response = ResponseUtil.success("Depot deleted", null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates a depot
     * 
     * @param id the unique identifier of the depot to update
     * @param depot the Depot object containing the name and description
     * @return a String indicating whether the depot was updated or not
     * @throws DepotNotFoundException
     * @throws DepotUpdateException
     * @throws GitNotFoundException
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<Depot>> update(@PathVariable Long id, @RequestBody Depot depot) throws DepotNotFoundException, DepotUpdateException, GitNotFoundException {
        logger.info("update");
            
        Depot updatedDepot = depotService.update(id, depot);

        ApiResponse<Depot> response = ResponseUtil.success("Depot updated", updatedDepot);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Clones a depot by its id into a new directory
     * @param id the unique identifier of the depot to clone
     * @return a String indicating whether the depot was cloned or not
     * @throws GitCloneException
     * @throws GitNotFoundException
     * @throws GitBranchException
     */
    @GetMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<String>> gitClone(@PathVariable Long id) throws GitCloneException, GitNotFoundException, GitBranchException {
        logger.info("clone");
            
        String msg = depotService.gitClone(id);

        ApiResponse<String> response = ResponseUtil.success("Depot cloned", msg);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Pulls a depot by its id
     * @param id the unique identifier of the depot to pull
     * @return a String indicating whether the depot was pulled or not
     * @throws GitPullException 
     * @throws GitNotFoundException 
     */
    @GetMapping("/{id}/pull")
    public ResponseEntity<ApiResponse<String>> gitPull(@PathVariable Long id) throws GitPullException, GitNotFoundException {
        logger.info("pull");
        
        String msg = depotService.gitPull(id);
        
        ApiResponse<String> response = ResponseUtil.success("Depot pulled", msg);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Display the code of a depot
     * @param id the unique identifier of the depot to display
     * @return a String indicating whether the depot was displayed or not
     * @throws GitCodeException 
     * @throws GitNotFoundException
     */
    @GetMapping("/{id}/code")
    public ResponseEntity<ApiResponse<List<File>>> gitCode(@PathVariable Long id) throws GitCodeException, GitNotFoundException {
        logger.info("code");

        List<File> files = depotService.gitCode(id);

        ApiResponse<List<File>> response = ResponseUtil.success("Code displayed", files);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes the code of a depot
     * @param id the unique identifier of the depot to delete
     * @return a String indicating whether the code was deleted or not
     * @throws GitDeleteException 
     * @throws GitNotFoundException 
     */
    @DeleteMapping("/{id}/code/delete")
    public ResponseEntity<ApiResponse<String>> gitCodeDelete(@PathVariable Long id) throws GitDeleteException, GitNotFoundException {
        logger.info("codeDelete");
        
        String msg = depotService.gitDelete(id);

        ApiResponse<String> response = ResponseUtil.success("Code deleted", msg);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Detects the technologies used in the code of a depot
     * @param id the unique identifier of the depot to analyze
     * @return a String indicating whether the depot was analyzed or not, and a Map containing the technologies used in the depot
     * @throws GitCodeException 
     * @throws GitNotFoundException 
     */
    @GetMapping("{id}/technologies")
    public ResponseEntity<ApiResponse<List<AbstractTechnology>>> gitCodeTechnologies(@PathVariable Long id) throws GitCodeException, GitNotFoundException {
        logger.info("depotTechnologies");
        
        List<AbstractTechnology> technologies = depotService.gitCodeTechnologies(id);

        ApiResponse<List<AbstractTechnology>> response = ResponseUtil.success("Depot technologies displayed", technologies);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Detects the dependencies used in the code of a depot
     * @param id the unique identifier of the depot to analyze
     * @return a String indicating whether the depot was analyzed or not, and a Map containing the dependencies used in the depot
     * @throws GitCodeException 
     * @throws GitNotFoundException 
     */
    @GetMapping("{id}/dependencies")
    public ResponseEntity<ApiResponse<Map<String, List<Dependency>>>> gitCodeDependencies(@PathVariable Long id) throws GitCodeException, GitNotFoundException {
        logger.info("depotDependencies");
        
        Map<String, List<Dependency>> dependencies = depotService.gitCodeDependencies(id);

        ApiResponse<Map<String, List<Dependency>>> response = ResponseUtil.success("Depot dependencies displayed", dependencies);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    /**
     * Updates the dependencies of a depot
     * @param id the unique identifier of the depot to update
     * @return a String indicating whether the dependencies were updated or not
     * @throws DepotUpdateException 
     * @throws GitCodeException
     * @throws GitPullException 
     * @throws DepotNotFoundException 
     */
    @PutMapping("/{id}/dependencies/update")
    public ResponseEntity<ApiResponse<String>> gitCodeDependenciesUpdate(@PathVariable Long id) throws DepotUpdateException, DepotNotFoundException, GitPullException, GitCodeException {
        logger.info("depotDependenciesUpdate");

        String msg = depotService.updateDepotDependencies(id);

        ApiResponse<String> response = ResponseUtil.success("Depot dependencies updated", msg);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Creates a pull request for a depot
     * @param id the unique identifier of the depot to create a pull request for
     * @return a String indicating whether the pull request was created or not
     * @throws GitPullRequestException
     * @throws GitNotFoundException
     */
    @PostMapping("/{id}/pullRequest")
    public ResponseEntity<ApiResponse<String>> gitPullRequest(@PathVariable Long id) throws GitPullRequestException, GitNotFoundException {
        logger.info("pullRequest");

        String msg = depotService.gitPullRequest(id);

        ApiResponse<String> response = ResponseUtil.success("Pull request created", msg);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
