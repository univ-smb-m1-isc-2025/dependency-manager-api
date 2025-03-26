package com.info803.dependency_manager_api.adapters.api;

import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.infrastructure.utils.TechnologyType;
import com.info803.dependency_manager_api.application.DepotService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.File;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/depots")
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
        try {
            List<Depot> depots = depotService.depotList();
            return ResponseEntity.ok(new ApiResponse<>("Depots retrieved", depots));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }
    
    /**
     * Retrives a single depot by its id
     * 
     * @param id the unique identifier of the depot to retrieve
     * @return the Depot object with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Optional<Depot>>> depot(@PathVariable Long id) {
        logger.info("depot");
        try {
            Optional<Depot> depot = depotService.depot(id);
            return ResponseEntity.ok(new ApiResponse<>("Depot retrieved", depot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Creates a new depot with the given depot information
     * 
     * @param depot the Depot object containing the name and description
     * @return a String indicating whether the depot was created or not
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> create(@RequestBody Depot depot) {
        logger.info("create");
        try {
            depotService.create(depot);
            return ResponseEntity.ok(new ApiResponse<>("Depot created"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Deletes a depot by its id
     * 
     * @param id the unique identifier of the depot to delete
     * @return a String indicating whether the depot was deleted or not
     */
    @GetMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        logger.info("delete");
        try {
            depotService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>("Depot deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Updates a depot
     * 
     * @param depot the Depot object containing the name and description
     * @return a String indicating whether the depot was updated or not
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody Depot depot) {
        logger.info("update");
        try {
            depotService.update(depot);
            return ResponseEntity.ok(new ApiResponse<>("Depot updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Clones a depot by its id into a new directory
     * @param id the unique identifier of the depot to clone
     * @return a String indicating whether the depot was cloned or not
     */
    @GetMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<String>> gitClone(@PathVariable Long id) {
        logger.info("clone");
        try {
            String msg = depotService.gitClone(id);
            return ResponseEntity.ok(new ApiResponse<>(msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Pulls a depot by its id
     * @param id the unique identifier of the depot to pull
     * @return a String indicating whether the depot was pulled or not
     */
    @GetMapping("/{id}/pull")
    public ResponseEntity<ApiResponse<String>> gitPull(@PathVariable Long id) {
        logger.info("pull");
        try {
            String msg = depotService.gitPull(id);
            return ResponseEntity.ok(new ApiResponse<>(msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Display the code of a depot
     * @param id the unique identifier of the depot to display
     * @return a String indicating whether the depot was displayed or not
     */
    @GetMapping("/{id}/code")
    public ResponseEntity<ApiResponse<List<File>>> gitCode(@PathVariable Long id) {
        logger.info("code");
        try {
            List<File> files = depotService.gitCode(id);
            return ResponseEntity.ok(new ApiResponse<>("Code displayed", files));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Deletes the code of a depot
     * @param id the unique identifier of the depot to delete
     * @return a String indicating whether the code was deleted or not
     */
    @GetMapping("/{id}/code/delete")
    public ResponseEntity<ApiResponse<String>> gitCodeDelete(@PathVariable Long id) {
        logger.info("codeDelete");
        try {
            String msg = depotService.gitDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Detects the technologies used in the code of a depot
     * @param id the unique identifier of the depot to analyze
     * @return a String indicating whether the depot was analyzed or not, and a Map containing the technologies used in the depot
     */
    @GetMapping("{id}/code/technology")
    public ResponseEntity<ApiResponse<Map<TechnologyType, List<String>>>> gitCodeTechnology(@PathVariable Long id) {
        logger.info("codeTechnology");
        try {
            Map<TechnologyType, List<String>> technologies = depotService.gitCodeTechnology(id);
            return ResponseEntity.ok(new ApiResponse<>("Code technology displayed", technologies));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    /**
     * Detects the dependencies used in the code of a depot
     * @param id the unique identifier of the depot to analyze
     * @return a String indicating whether the depot was analyzed or not, and a Map containing the dependencies used in the depot
     */
    @GetMapping("{id}/code/dependency")
    public ResponseEntity<ApiResponse<Map<TechnologyType, Map<String, String>>>> gitCodeDependency(@PathVariable Long id) {
        logger.info("codeDependency");
        try {
            Map<TechnologyType, Map<String, String>> dependencies = depotService.gitCodeDependency(id);
            return ResponseEntity.ok(new ApiResponse<>("Code dependency displayed", dependencies));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), 400));
        }
    }
    
}
