package com.example.repository;

import com.example.model.DocumentMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentMetadataRepository extends ElasticsearchRepository<DocumentMetadata, String> {
    Page<DocumentMetadata> findByFilenameContaining(String filename, Pageable pageable);
    List<DocumentMetadata> findByContentType(String contentType);
    List<DocumentMetadata> findByUploadDateBetween(LocalDateTime start, LocalDateTime end);
    Optional<DocumentMetadata> findByStorageKey(String storageKey);
    
}
