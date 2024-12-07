package com.example.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DocumentUploadRequest {
    private String filename;
    private String contentType;
    private byte[] content;
    private Map<String, String> metadata;
}