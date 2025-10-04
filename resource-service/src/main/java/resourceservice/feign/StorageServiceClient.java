package resourceservice.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import request.storage.StorageRequest;
import response.storage.StorageResponse;

import java.util.Map;

@FeignClient(name = "storage-service")
public interface StorageServiceClient {

    @GetMapping("storage-service/storages/{storageType}")
    ResponseEntity<StorageResponse> getStorageByType(@PathVariable("storageType") String storageType);

    @PostMapping("storage-service/storages")
    ResponseEntity<Map<String, Object>> createStorage(@Valid @RequestBody StorageRequest storage);
}
