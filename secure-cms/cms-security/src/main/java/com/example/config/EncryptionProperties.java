package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "encryption")
public class EncryptionProperties {
    private String masterKey;
}