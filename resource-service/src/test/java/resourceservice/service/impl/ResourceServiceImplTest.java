package resourceservice.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import resourceservice.entity.ResourceEntity;
import resourceservice.entity.StorageType;
import resourceservice.exception.ResourceNotFoundException;
import resourceservice.feign.SongServiceClient;
import resourceservice.messaging.ResourceUploadedProducer;
import resourceservice.repository.ResourceRepository;
import resourceservice.service.S3Service;
import resourceservice.service.StorageServiceWrapper;
import response.storage.StorageResponse;

import java.util.Random;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static resourceservice.entity.StorageType.STAGING;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceUploadedProducer resourceUploadedProducer;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private StorageServiceWrapper storageServiceWrapper;

    @InjectMocks private ResourceServiceImpl resourceService;

    @Test
    void saveResource_ToStaging_savesAndUploadsAndSends() {
        byte[] file = new byte[]{1,2,3};
        String bucketKey = UUID.randomUUID().toString() + ".mp3";
        String fileUrl = "http://s3/file.mp3";
        ResourceEntity entity = new ResourceEntity(bucketKey, fileUrl, StorageType.STAGING);
        Integer integer = new Random().nextInt();
        entity.setId(integer);
        StorageResponse storageResponse = new StorageResponse();
        storageResponse.setBucket("s3");
        ResponseEntity<StorageResponse> storage = ResponseEntity.of(Optional.of(storageResponse));

        when(s3Service.getFileUrl(anyString(), eq("s3"))).thenReturn(fileUrl);
        when(resourceRepository.save(any())).thenReturn(entity);
        when(storageServiceWrapper.getStorageByType(STAGING.name())).thenReturn(storage);
        when(s3Service.bucketExists("s3")).thenReturn(true);

        ResourceEntity result = resourceService.saveResourceToStaging(file);

        assertEquals(fileUrl, result.getFileUrl());
        verify(s3Service).getFileUrl(anyString(), eq("s3"));
        verify(resourceRepository).save(any(ResourceEntity.class));
        verify(s3Service).uploadFile(anyString(), eq(file), eq("s3"));
        verify(resourceUploadedProducer).sendResourceId(anyString());
    }

    @Test
    void getResourceById_found_returnsPair() {
        int id = 1;
        String bucketKey = "key";
        byte[] file = new byte[]{1};
        ResourceEntity entity = new ResourceEntity(bucketKey, "url", StorageType.PERMANENT);
        entity.setFileUrl("http://localstack:4566/staging-bucket");
        when(resourceRepository.findById(id)).thenReturn(Optional.of(entity));
        when(s3Service.downloadFile(bucketKey, "staging-bucket")).thenReturn(file);

        Pair<String, byte[]> result = resourceService.getResourceById(id);

        assertEquals(bucketKey, result.getLeft());
        assertArrayEquals(file, result.getRight());
        verify(resourceRepository).findById(id);
        verify(s3Service).downloadFile(bucketKey, "staging-bucket");
    }

    @Test
    void getResourceById_notFound_throws() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(1));

        verify(resourceRepository).findById(1);
    }

    @Test
    void deleteByIds_returnsRemovedIds() {
        String ids = "1,2,3";
        when(resourceRepository.existsById(1)).thenReturn(true);
        when(resourceRepository.existsById(2)).thenReturn(true);
        List<ResourceEntity> resourceEntities = generateResources(2);
        when(resourceRepository.findAllById(List.of(1, 2))).thenReturn(resourceEntities);
        doNothing().when(s3Service).deleteFile(anyString(), eq(null));
        doNothing().when(resourceRepository).deleteAllByIdInBatch(List.of(1, 2));

        List<Integer> resultIds = resourceService.deleteByIds(ids, "s3");

        assertEquals(resultIds, List.of(1, 2));
        verify(resourceRepository).existsById(1);
        verify(resourceRepository).existsById(2);
        verify(resourceRepository).findAllById(List.of(1, 2));
        verify(s3Service, times(2)).deleteFile(anyString(), eq(null));
        verify(resourceRepository).deleteAllByIdInBatch(List.of(1, 2));
    }

    @Test
    void resourceExists_returnsTrue() {
        when(resourceRepository.existsById(1)).thenReturn(true);

        boolean result = resourceService.resourceExists("1");

        assertTrue(result);
        verify(resourceRepository).existsById(1);
    }

    private List<ResourceEntity> generateResources(int numberOfResources) {
        if (numberOfResources == 0) {
            throw new IllegalArgumentException("illegal numberOfResources");
        }
        List<ResourceEntity> resourceEntities = new ArrayList<>();
        for (int i = 0; i < numberOfResources; i++) {
            ResourceEntity resource = new ResourceEntity();
            resource.setBucketKey(UUID.randomUUID().toString());
            resourceEntities.add(resource);
        }
        return resourceEntities;
    }
}
