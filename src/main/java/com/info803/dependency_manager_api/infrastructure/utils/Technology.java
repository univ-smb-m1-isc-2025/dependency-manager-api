package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Technology {

    private Technology() {
        // None
    }

    /**
     * Détecte les technologies utilisées dans un répertoire donné.
     * @param repoPath Chemin du répertoire du projet
     * @return Les technologies utilisées dans le répertoire sous la forme d'un dictionnaire de paires "technologie" -> "liste des fichiers reconnus"
    */
    public static List<String> detectTechnologies(String repoPath) {
        File repoDirectory = new File(repoPath);
        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            return Collections.emptyList();
        }

        File[] filesInRepo = repoDirectory.listFiles();
        if (filesInRepo == null) return Collections.emptyList();

        // Liste des technologies détectées
        return Arrays.stream(TechnologyType.values())
                .filter(tech -> Arrays.stream(tech.getFiles())
                        .anyMatch(fileName -> new File(repoDirectory, fileName).exists()))
                .map(TechnologyType::getName)
                .toList();
    }


    // public static List<TechnologyType> detectTechnologies(String repoPath) {
    //     File repoDirectory = new File(repoPath);
    //     if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
    //         return Collections.emptyList();
    //     }

    //     File[] filesInRepo = repoDirectory.listFiles();
    //     if (filesInRepo == null) return Collections.emptyList();

    //     // Liste des technologies détectées
    //     List<TechnologyType> detectedTechnologies = Arrays.stream(TechnologyType.values())
    //             .filter(tech -> Arrays.stream(tech.getFiles())
    //                     .anyMatch(fileName -> new File(repoDirectory, fileName).exists()))
    //             .collect(Collectors.toList());

    //     return detectedTechnologies;
    // }
}
