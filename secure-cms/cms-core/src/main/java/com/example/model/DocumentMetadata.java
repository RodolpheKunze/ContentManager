package com.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(indexName = "documents")
public class DocumentMetadata {
    @Id
    private String id;
    
    @Field(type = FieldType.Text)
    private String filename;
    
    @Field(type = FieldType.Text)
    private String contentType;
    
    @Field(type = FieldType.Keyword)
    private String storageKey;
    
    @Field(type = FieldType.Long)
    private long size;
    
    @Field(type = FieldType.Date)
    private LocalDateTime uploadDate;
    
    @Field(type = FieldType.Date)
    private LocalDateTime lastModifiedDate;
    
    @Field(type = FieldType.Object)
    private Map<String, String> customMetadata;
}
