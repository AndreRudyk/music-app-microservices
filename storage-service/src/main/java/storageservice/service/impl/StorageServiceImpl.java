package storageservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import storageservice.converter.StorageConverter;
import storageservice.domain.Storage;
import storageservice.entity.StorageEntity;
import storageservice.repository.StorageRepository;
import storageservice.service.StorageService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    private final StorageConverter storageConverter;

    @Override
    public Storage createStorage(Storage storage) {
        StorageEntity storageEntity = storageConverter.domainToEntity(storage);
        return storageConverter.entityToDomain(storageRepository.save(storageEntity));
    }

    @Override
    public List<Storage> getAllStorages() {
        return storageRepository.findAll().stream()
                .map(storageConverter::entityToDomain)
                .toList();
    }

    @Override
    public void deleteStorages(List<Integer> ids) {
        storageRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Optional<Storage> getByType(String type) {
        log.info("Retrieving storage by type: {}", type );
        return storageRepository.findByStorageType(type.toUpperCase()).stream()
                .filter(storage -> type.equalsIgnoreCase(storage.getStorageType()))
                .map(storageConverter::entityToDomain)
                .findFirst();
    }
}
