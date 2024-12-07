package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.config.EncryptionProperties;
import com.example.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class, EncryptionProperties.class})
public class CmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}