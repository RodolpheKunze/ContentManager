package com.example.api.controller;

import com.example.api.dto.DocumentUploadRequest;
import com.example.api.dto.PageResponse;
import com.example.model.DocumentMetadata;
import com.example.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentMetadata> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("metadata") String metadataJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> metadata = mapper.readValue(metadataJson, Map.class);
            
            DocumentMetadata result = documentService.uploadDocument(
                file.getOriginalFilename(),
                file.getBytes(),
                file.getContentType(),
                metadata
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{storageKey}")
    public ResponseEntity<DocumentMetadata> getMetadata(@PathVariable String storageKey) {
        DocumentMetadata metadata = documentService.getDocumentMetadata(storageKey);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/{storageKey}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String storageKey) {
        try {
            DocumentMetadata metadata = documentService.getDocumentMetadata(storageKey);
            byte[] content = documentService.downloadDocument(storageKey);
            
            if (content == null || content.length == 0) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + metadata.getFilename() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, 
                    HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_OCTET_STREAM) 
                .contentLength(metadata.getSize())
                .body(content);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<PageResponse<DocumentMetadata>> searchDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<DocumentMetadata> results = query != null && !query.trim().isEmpty() 
            ? documentService.searchDocuments(query, pageable)
            : documentService.getAllDocuments(pageable);
            
        return ResponseEntity.ok(PageResponse.from(results));
    }
}