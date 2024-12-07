package com.example.encryption;

public interface EncryptionService {
    String encrypt(byte[] content, String masterKey) throws Exception;
    byte[] decrypt(String encryptedContent, String masterKey) throws Exception;
}
    

