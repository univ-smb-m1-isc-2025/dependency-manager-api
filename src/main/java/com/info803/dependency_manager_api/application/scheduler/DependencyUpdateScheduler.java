package com.info803.dependency_manager_api.application.scheduler;

import com.info803.dependency_manager_api.application.DepotService;
import com.info803.dependency_manager_api.infrastructure.persistence.depot.Depot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DependencyUpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DependencyUpdateScheduler.class);

    private final DepotService depotService;

    // Rend la planification configurable via application.properties
    // Exemple de valeur par défaut : tous les jours à 2h du matin
    @Value("${dependency.update.schedule.cron:0 0 2 * * ?}")
    private String cronExpression;

    @Autowired
    public DependencyUpdateScheduler(DepotService depotService) {
        this.depotService = depotService;
    }

    // Utilise l'expression cron définie dans application.properties
    @Scheduled(cron = "${dependency.update.schedule.cron}")
    public void updateAllDepotDependencies() {
        logger.info("Starting scheduled dependency update routine...");

        List<Depot> depots = depotService.depotList();
        if (depots.isEmpty()) {
            logger.info("No depots found to update.");
            return;
        }

        logger.info("Found {} depots to process.", depots.size());

        for (Depot depot : depots) {
            Long depotId = depot.getId();
            String depotName = depot.getName();
            logger.info("Processing depot ID: {}, Name: {}", depotId, depotName);

            try {
                depotService.updateDepotDependencies(depotId);

            } catch (Exception e) {
                logger.error("Failed to process depot ID: {}, Name: {}. Error: {}", depotId, depotName, e.getMessage(), e);
            }
             // Ajouter une pause entre les dépôts pour ne pas surcharger les API Git ou le système
            try {
                Thread.sleep(10000); // 10 secondes entre chaque dépôt, à ajuster
            } catch (InterruptedException ie) {
                logger.warn("Scheduler interrupted during sleep between depots.");
                Thread.currentThread().interrupt();
                break; 
            }
        }

        logger.info("Finished scheduled dependency update routine.");
    }
}
