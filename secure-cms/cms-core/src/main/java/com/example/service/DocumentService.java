
package com.example.service;

import com.example.model.DocumentMetadata;
import com.example.repository.DocumentMetadataRepository;
import com.example.storage.service.StorageService;
import com.example.exception.DocumentNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


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
    
    public Page<DocumentMetadata> searchDocuments(String searchTerm, Pageable pageable) {

        System.out.println("Searching with term: " + searchTerm);
        System.out.println("Page request: " + pageable);
    
        NativeQuery searchQuery = NativeQuery.builder() 
            .withQuery(q -> q .wildcard(w -> w 
            .field("filename") .value("*" + searchTerm + "*") ) ) 
            .withPageable(pageable) 
            .build();

        /*NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(q -> q
                .bool(b -> b
                    .should(s -> s
                        .multiMatch(m -> m
                            .query(searchTerm)
                            .fields(List.of("filename^3", "contentType^2", "customMetadata"))
                            .fuzziness("AUTO")
                        )
                    )
                )
            )
            .withPageable(pageable)
            .build();
        */
        System.out.println("Search query: " + searchQuery);
        SearchHits<DocumentMetadata> searchHits = elasticsearchOperations.search(
            searchQuery, 
            DocumentMetadata.class
        );
        
        System.out.println("Total hits: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> 
            System.out.println("Found document: " + hit.getContent().getFilename())
        );

        List<DocumentMetadata> content = searchHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
            
        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

    public DocumentMetadata getDocumentMetadata(String storageKey) {
        return metadataRepository.findByStorageKey(storageKey)
            .orElseThrow(() -> new DocumentNotFoundException(
                "Document not found with storage key: " + storageKey));
    }

    public Page<DocumentMetadata> getAllDocuments(Pageable pageable) {
        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(q -> q
                .matchAll(m -> m)
            )
            .withPageable(pageable)
            .build();

        SearchHits<DocumentMetadata> searchHits = elasticsearchOperations.search(
            searchQuery, 
            DocumentMetadata.class
        );

        List<DocumentMetadata> content = searchHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
            
        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

public void debugElasticsearchIndex() {
    // Get mapping
    IndexOperations indexOps = elasticsearchOperations.indexOps(DocumentMetadata.class);
    System.out.println("Index Mapping: " + indexOps.getMapping());
    
    // Get all documents
    NativeQuery getAllQuery = NativeQuery.builder()
        .withQuery(q -> q.matchAll(m -> m))
        .build();
        
    SearchHits<DocumentMetadata> allDocs = elasticsearchOperations.search(
        getAllQuery, 
        DocumentMetadata.class
    );
    
    System.out.println("Total documents in index: " + allDocs.getTotalHits());
    allDocs.getSearchHits().forEach(hit -> 
        System.out.println("Document in index: " + hit.getContent().getFilename())
    );
}
}