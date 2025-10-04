package storageservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.storage.StorageResponse;
import storageservice.converter.StorageConverter;
import storageservice.domain.Storage;
import request.storage.StorageRequest;
import storageservice.service.StorageService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    private final StorageConverter storageConverter;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStorage(@Valid @RequestBody StorageRequest storage) {
        if (storage.getStorageType() == null || storage.getBucket() == null || storage.getPath() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }
        Storage created = storageService.createStorage(storageConverter.requestToDomain(storage));
        return ResponseEntity.ok(Map.of("id", created.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Storage>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteStorages(@RequestParam String id) {
        if (id.length() > 200) {
            return ResponseEntity.badRequest().body(Map.of("error", "CSV string too long"));
        }
        List<Integer> ids;
        try {
            ids = Arrays.stream(id.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid CSV format"));
        }
        storageService.deleteStorages(ids);
        return ResponseEntity.ok(Map.of("deleted", ids));
    }

    @GetMapping("/{storageType}")
    public ResponseEntity<StorageResponse> getStorageByType(@PathVariable("storageType") String storageType) {
        return storageService.getByType(storageType)
                .map(storageConverter::domainToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
