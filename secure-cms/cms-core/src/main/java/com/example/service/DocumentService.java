
package com.example.service;

import com.example.model.DocumentMetadata;
import com.example.repository.DocumentMetadataRepository;
import com.example.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DocumentService {
    private final StorageService storageService;
    private final DocumentMetadataRepository metadataRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public DocumentMetadata uploadDocument(String filename, byte[] content, 
            String contentType, Map<String, String> customMetadata) throws Exception {
        // Generate storage key
        String storageKey = UUID.randomUUID().toString();
        
        // Upload encrypted content
        storageService.uploadDocument(storageKey, content);
        
        // Save metadata
        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setFilename(filename);
        metadata.setContentType(contentType);
        metadata.setStorageKey(storageKey);
        metadata.setSize(content.length);
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setLastModifiedDate(LocalDateTime.now());
        metadata.setCustomMetadata(customMetadata);
        
        return metadataRepository.save(metadata);
    }
    
    public byte[] downloadDocument(String storageKey) throws Exception {
        DocumentMetadata metadata = metadataRepository.findByStorageKey(storageKey)
            .orElseThrow(() -> new RuntimeException("Document not found"));
            byte[] content = storageService.downloadDocument(storageKey);
        return storageService.downloadDocument(storageKey);
    }
    
    public Page<DocumentMetadata> searchDocuments(String query, Pageable pageable) {
        return metadataRepository.findByFilenameContaining(query, pageable);
    }

    public DocumentMetadata getDocumentMetadata(String storageKey) {
        return metadataRepository.findByStorageKey(storageKey)
            .orElseThrow(() -> new RuntimeException("Document not found: " + storageKey));
    }


}