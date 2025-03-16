package com.info803.dependency_manager_api.adapters.api;

import com.info803.dependency_manager_api.infrastructure.persistence.Depot;
import com.info803.dependency_manager_api.application.DepotService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




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
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
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
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
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
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
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
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
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
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<String>> gitClone(@PathVariable Long id) {
        logger.info("clone");
        try {
            String msg = depotService.gitClone(id);
            return ResponseEntity.ok(new ApiResponse<>(msg));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }
}
