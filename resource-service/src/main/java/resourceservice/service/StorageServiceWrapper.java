package resourceservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import request.storage.StorageRequest;
import resourceservice.feign.StorageServiceClient;
import response.storage.StorageResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceWrapper {

    private final StorageServiceClient storageServiceClient;

    @CircuitBreaker(name = "storageService", fallbackMethod = "getStorageByTypeFallback")
    public ResponseEntity<StorageResponse> getStorageByType(String storageType) {
        log.info("Calling Storage Service for storage type: {}", storageType);
        return storageServiceClient.getStorageByType(storageType);
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "createStorageFallback")
    public ResponseEntity<Map<String, Object>> createStorage(StorageRequest storage) {
        log.info("Calling Storage Service to create storage: {}", storage);
        return storageServiceClient.createStorage(storage);
    }

    public ResponseEntity<StorageResponse> getStorageByTypeFallback(String storageType, Exception ex) {
        log.warn("Storage Service is unavailable, using fallback for storage type: {}. Reason: {}",
                storageType, ex.getMessage());

        StorageResponse fallbackResponse = createFallbackStorageResponse(storageType);
        return ResponseEntity.ok(fallbackResponse);
    }

    public ResponseEntity<Map<String, Object>> createStorageFallback(StorageRequest storage, Exception ex) {
        log.warn("Storage Service is unavailable, using fallback for create storage. Reason: {}",
                ex.getMessage());

        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("id", 999);
        fallbackResponse.put("storageType", storage.getStorageType());
        fallbackResponse.put("bucket", getFallbackBucketName(storage.getStorageType()));
        fallbackResponse.put("path", storage.getPath() != null ? storage.getPath() : "/fallback");
        fallbackResponse.put("status", "created_offline");
        fallbackResponse.put("message", "Storage created in fallback mode due to service unavailability");

        return ResponseEntity.ok(fallbackResponse);
    }

    private StorageResponse createFallbackStorageResponse(String storageType) {
        StorageResponse response = new StorageResponse();
        switch (storageType.toUpperCase()) {
            case "STAGING":
                response.setId(1);
                response.setStorageType("STAGING");
                response.setBucket("staging-bucket");
                response.setPath("http://localstack:4566/staging-bucket");
                break;
            case "PERMANENT":
                response.setId(2);
                response.setStorageType("PERMANENT");
                response.setBucket("permanent-bucket");
                response.setPath("http://localstack:4566/permanent-bucket");
                break;
            default:
                response.setId(0);
                response.setStorageType(storageType);
                response.setBucket("default-bucket-fallback");
                response.setPath("/default");
                break;
        }
        log.info("Created fallback storage response: {}", response);
        return response;
    }

    private String getFallbackBucketName(String storageType) {
        return switch (storageType.toUpperCase()) {
            case "STAGING" -> "staging-bucket";
            case "PERMANENT" -> "permanent-bucket";
            default -> "default-bucket-fallback";
        };
    }
}
