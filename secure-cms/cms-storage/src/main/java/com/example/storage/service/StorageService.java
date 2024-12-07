package com.example.storage.service;

import com.example.config.StorageProperties;
import com.example.encryption.EncryptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final EncryptionService encryptionService;
    private final StorageProperties storageProperties;
    
    @Value("${storage.endpoint}")
    private String endpoint;
    
    @Value("${storage.access-key}")
    private String accessKey;
    
    @Value("${storage.secret-key}")
    private String secretKey;
    
    @Value("${storage.bucket}")
    private String bucket;
    
    @Value("${encryption.master-key}")
    private String masterKey;
    
    private S3Client s3Client;
    
    private S3Client getS3Client() {
        if (s3Client == null) {
            s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1) // Can be any region, since we're using custom endpoint
                .forcePathStyle(true) // Required for some S3-compatible storages
                .build();
        }
        return s3Client;
    }
    
    public void uploadDocument(String key, byte[] content) throws Exception {
        // Encrypt content
        String encryptedContent = encryptionService.encrypt(content, masterKey);
        
        // Upload to S3
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
            
        getS3Client().putObject(request, RequestBody.fromString(encryptedContent));
    }
    
    public byte[] downloadDocument(String key) throws Exception {
        // Download from S3
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
            
        String encryptedContent = getS3Client().getObject(request)
            .readAllBytes()
            .toString();
            
        // Decrypt content
        return encryptionService.decrypt(encryptedContent, masterKey);
    }

    public void deleteDocument(String key) throws Exception {
    s3Client.deleteObject(DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build());
    }
}