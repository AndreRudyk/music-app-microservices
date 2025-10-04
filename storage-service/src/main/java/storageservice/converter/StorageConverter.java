package storageservice.converter;

import org.mapstruct.Mapper;
import response.storage.StorageResponse;
import storageservice.domain.Storage;
import storageservice.entity.StorageEntity;
import request.storage.StorageRequest;

@Mapper(componentModel = "spring")
public interface StorageConverter {

    Storage entityToDomain(StorageEntity entity);

    StorageEntity domainToEntity(Storage storage);

    Storage requestToDomain(StorageRequest request);

    StorageResponse domainToResponse(Storage storage);
}
