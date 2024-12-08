package com.example.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.service.DocumentService;  

@Configuration
public class ElasticsearchConfig {
    
    private final DocumentService documentService;

    public ElasticsearchConfig(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Bean
    public CommandLineRunner elasticsearchDebugRunner() {
        return args -> {
            System.out.println("Debugging Elasticsearch index at startup...");
            documentService.debugElasticsearchIndex();
        };
    }
}