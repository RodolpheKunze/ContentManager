package com.example.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import java.security.Security;
import jakarta.annotation.*;

@Data
@Configuration
@ConfigurationProperties(prefix = "encryption")
public class EncryptionConfig {
    private String masterKey;
    private int keySize = 256;
    private int iterations = 65536;
    private String algorithm = "AES/GCM/NoPadding";
    private String provider = "BC";
    private int gcmTagLength = 128;
    private int ivLength = 12;
    private int saltLength = 16;

    @PostConstruct
    public void init() {
        // Register Bouncy Castle provider if not already registered
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
