package storageservice.service;

import storageservice.domain.Storage;

import java.util.List;
import java.util.Optional;

public interface StorageService {

    Storage createStorage(Storage request);

    List<Storage> getAllStorages();

    void deleteStorages(List<Integer> ids);

    Optional<Storage> getByType(String type);
}
