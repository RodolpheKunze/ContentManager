package com.example.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int SALT_LENGTH = 16;
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String encrypt(byte[] content, String masterKey) throws Exception {
        // Generate a random salt
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        
        // Generate encryption key from master key and salt
        SecretKey key = deriveKey(masterKey, salt);
        
        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        
        // Add salt as additional authenticated data
        cipher.updateAAD(salt);
        
        // Encrypt
        byte[] cipherText = cipher.doFinal(content);
        
        // Combine salt + IV + ciphertext
        byte[] message = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy(salt, 0, message, 0, salt.length);
        System.arraycopy(iv, 0, message, salt.length, iv.length);
        System.arraycopy(cipherText, 0, message, salt.length + iv.length, cipherText.length);
        
        return Base64.getEncoder().encodeToString(message);
    }
    
    public byte[] decrypt(String encryptedContent, String masterKey) throws Exception {
        byte[] message = Base64.getDecoder().decode(encryptedContent);
        
        // Extract salt, IV, and ciphertext
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] cipherText = new byte[message.length - SALT_LENGTH - GCM_IV_LENGTH];
        
        System.arraycopy(message, 0, salt, 0, salt.length);
        System.arraycopy(message, salt.length, iv, 0, iv.length);
        System.arraycopy(message, salt.length + iv.length, cipherText, 0, cipherText.length);
        
        // Derive key from master key and salt
        SecretKey key = deriveKey(masterKey, salt);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        
        // Add salt as additional authenticated data
        cipher.updateAAD(salt);
        
        // Decrypt
        return cipher.doFinal(cipherText);
    }
    
    private SecretKey deriveKey(String masterKey, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, 65536, 256);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}