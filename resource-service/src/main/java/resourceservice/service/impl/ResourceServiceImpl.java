package resourceservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import request.storage.StorageRequest;
import resourceservice.entity.ResourceEntity;
import resourceservice.entity.StorageType;
import resourceservice.exception.ResourceNotFoundException;
import resourceservice.feign.SongServiceClient;
import resourceservice.feign.StorageServiceClient;
import resourceservice.repository.ResourceRepository;
import resourceservice.service.S3Service;
import resourceservice.service.ResourceService;
import resourceservice.messaging.ResourceUploadedProducer;
import resourceservice.util.UrlParser;
import response.storage.StorageResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static resourceservice.entity.StorageType.STAGING;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceUploadedProducer resourceUploadedProducer;
    private final SongServiceClient songServiceClient;
    private final StorageServiceClient storageServiceClient;
    private final static String STATIC_BUCKET_NAME = "staging-bucket";

    @Override
    public ResourceEntity saveResourceToStaging(byte[] file) {
        String bucketKey = UUID.randomUUID().toString() + ".mp3";
        log.info("Saving file : {}", bucketKey);
        ResponseEntity<StorageResponse> storage = storageServiceClient.getStorageByType(STAGING.name());
        StorageResponse storageResponse = storage.getBody();
        assert storageResponse != null;
        String bucketName = storageResponse.getBucket();
        if (!s3Service.bucketExists(bucketName)) {
            s3Service.createBucket(bucketName);
        }
        String fileUrl = s3Service.getFileUrl(bucketKey, bucketName);
        ResourceEntity savedResource = resourceRepository.save(new ResourceEntity(bucketKey, fileUrl, STAGING));
        s3Service.uploadFile(bucketKey, file, bucketName);
        resourceUploadedProducer.sendResourceId(savedResource.getId().toString());
        return savedResource;
    }

    @Override
    public Pair<String, byte[]> getResourceById(Integer id) {
        log.info("Calling getResourceById with id: {}", id);
        return resourceRepository.findById(id)
                .map(resourceEntity -> Pair.of(resourceEntity.getBucketKey(), s3Service.downloadFile(resourceEntity.getBucketKey(),
                        UrlParser.extractBucketName(resourceEntity.getFileUrl()))))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found!"));
    }

    @Override
    public List<Integer> deleteByIds(String ids, String bucket) {
        List<Integer> existingIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .filter(resourceRepository::existsById)
                .toList();
        resourceRepository.findAllById(existingIds)
                .forEach(resourceEntity -> s3Service.deleteFile(resourceEntity.getBucketKey(),
                        UrlParser.extractBucketName(resourceEntity.getFileUrl())));
        resourceRepository.deleteAllByIdInBatch(existingIds);
        songServiceClient.deleteSongMetadata(ids);
        return existingIds;
    }

    @Override
    public boolean resourceExists(String id) {
        return resourceRepository.existsById(Integer.parseInt(id));
    }

    @Override
    @Transactional
    public void updateStorageTypeByResourceId(String resourceId, StorageType storageType) {
        ResourceEntity storedResourceEntity = resourceRepository.findById(Integer.valueOf(resourceId))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        ResponseEntity<StorageResponse> currentStorageResponseEntity
                = storageServiceClient.getStorageByType(storedResourceEntity.getStorageType().name());
        StorageResponse storedStorageResponse = currentStorageResponseEntity.getBody();
        assert storedStorageResponse != null;

        ResponseEntity<StorageResponse> storageResponseEntity
                = storageServiceClient.getStorageByType(storageType.name());
        StorageResponse storageResponse = storageResponseEntity.getBody();
        assert storageResponse != null;
        if (!s3Service.bucketExists(storageResponse.getBucket())) {
            s3Service.createBucket(storageResponse.getBucket());
        }
        byte[] storedFile = s3Service.downloadFile(storedResourceEntity.getBucketKey(), storedStorageResponse.getBucket());
        s3Service.uploadFile(storedResourceEntity.getBucketKey(), storedFile, storageResponse.getBucket());

        s3Service.deleteFile(storedResourceEntity.getBucketKey(), storedStorageResponse.getBucket());

        storedResourceEntity.setStorageType(storageType);
        resourceRepository.save(storedResourceEntity);
    }
}
