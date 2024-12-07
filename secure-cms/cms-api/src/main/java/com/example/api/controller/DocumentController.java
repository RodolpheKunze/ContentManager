package com.example.api.controller;

import com.example.api.dto.DocumentUploadRequest;
import com.example.model.DocumentMetadata;
import com.example.service.DocumentService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestBody DocumentUploadRequest request) {
        try {
            documentService.uploadDocument(
                request.getFilename(),
                request.getContent(),
                request.getContentType(),
                request.getMetadata()
            );
            return ResponseEntity.ok("Document uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/{storageKey}")
    public ResponseEntity<DocumentMetadata> getMetadata(@PathVariable String storageKey) {
        DocumentMetadata metadata = documentService.getDocumentMetadata(storageKey);
        return ResponseEntity.ok(metadata);
    }
}