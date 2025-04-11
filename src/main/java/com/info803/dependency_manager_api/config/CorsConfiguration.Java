package com.info803.dependency_manager_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        // Créer une configuration CORS
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedOrigin("http://localhost:8080");
        corsConfig.addAllowedOrigin("http://www.dependency-manager.oups.net");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*"); // Autoriser tous les en-têtes
        corsConfig.setAllowCredentials(true); // Si vous devez autoriser les cookies ou l'authentification

        // Appliquer la configuration à toutes les URLs
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source); // Créer et retourner un CorsFilter
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedOrigins("http://localhost:8080")
                .allowedOrigins("http://www.dependency-manager.oups.net")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}