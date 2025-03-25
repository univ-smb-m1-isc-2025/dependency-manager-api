package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class Technology {

    private Technology() {
        // None
    }

    /**
     * Détecte la technologie utilisée dans un répertoire donné.
     * @param repoPath Chemin du répertoire du projet
     * @return Le type de technologie détecté
    */
    public static TechnologyType detectTechnology(String repoPath) {
        File repoDirectory = new File(repoPath);
        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            return TechnologyType.UNKNOWN;
        }

        File[] filesInRepo = repoDirectory.listFiles();
        if (filesInRepo == null) return TechnologyType.UNKNOWN;

        // Vérifier chaque type de technologie en fonction des fichiers caractéristiques
        Optional<TechnologyType> detectedType = Arrays.stream(TechnologyType.values())
                .filter(tech -> Arrays.stream(tech.getFiles())
                        .anyMatch(fileName -> new File(repoDirectory, fileName).exists()))
                .findFirst();

        return detectedType.orElse(TechnologyType.UNKNOWN);
    }
}
