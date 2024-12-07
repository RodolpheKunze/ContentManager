package com.example.encryption.exception;

public class EncryptionException extends RuntimeException {
    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class KeyGenerationException extends EncryptionException {
        public KeyGenerationException(String message, Throwable cause) {
            super("Failed to generate encryption key: " + message, cause);
        }
    }

    public static class EncryptionOperationException extends EncryptionException {
        public EncryptionOperationException(String message, Throwable cause) {
            super("Encryption operation failed: " + message, cause);
        }
    }

    public static class DecryptionOperationException extends EncryptionException {
        public DecryptionOperationException(String message, Throwable cause) {
            super("Decryption operation failed: " + message, cause);
        }
    }
}
