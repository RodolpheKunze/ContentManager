package com.example.api.controller;

import com.example.api.dto.DocumentUploadRequest;
import com.example.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}