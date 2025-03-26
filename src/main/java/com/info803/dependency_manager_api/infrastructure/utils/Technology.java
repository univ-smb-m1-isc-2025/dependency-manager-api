package com.info803.dependency_manager_api.infrastructure.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static Map<String, List<String>> detectTechnologies(String repoPath) {
        File repoDirectory = new File(repoPath);
        if (!repoDirectory.exists() || !repoDirectory.isDirectory()) {
            return Collections.emptyMap();
        }

        File[] filesInRepo = repoDirectory.listFiles();
        if (filesInRepo == null) return Collections.emptyMap();

        Map<String, List<String>> detectedTechnologies = new HashMap<>();

        for (TechnologyType tech : TechnologyType.values()) {
            List<String> matchedFiles = Arrays.stream(tech.getFiles())
                    .map(fileName -> new File(repoDirectory, fileName))
                    .filter(File::exists)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());

            if (!matchedFiles.isEmpty()) {
                detectedTechnologies.put(tech.getName(), matchedFiles);
            }
        }

        return detectedTechnologies;
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
