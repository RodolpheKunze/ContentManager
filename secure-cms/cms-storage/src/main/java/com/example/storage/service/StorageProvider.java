package com.example.storage.service;

public interface StorageProvider {
    void store(String key, byte[] content) throws Exception;
    byte[] retrieve(String key) throws Exception;

}
