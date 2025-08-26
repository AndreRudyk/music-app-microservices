package resourceservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import resourceservice.client.feign.SongServiceClient;
import resourceservice.entity.ResourceEntity;
import resourceservice.exception.ResourceNotFoundException;
import resourceservice.repository.ResourceRepository;
import resourceservice.service.S3Service;
import resourceservice.service.CreateSongRequestService;
import resourceservice.service.ResourceService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    private final CreateSongRequestService createSongRequestService;

    private final SongServiceClient songServiceClient;

    private final S3Service s3Service;

    @Override
    public ResourceEntity saveResource(byte[] file) {
        String bucketKey = UUID.randomUUID().toString() + ".mp3";
        log.info("Saving file : {}", bucketKey);
        String fileUrl = s3Service.getFileUrl(bucketKey);
        ResourceEntity savedResource = resourceRepository.save(new ResourceEntity(bucketKey, fileUrl));
        s3Service.uploadFile(bucketKey, file);
        return savedResource;
    }

    @Override
    public Pair<String, byte[]> getResourceById(Integer id) {
        return resourceRepository.findById(id)
                .map(resourceEntity -> Pair.of(resourceEntity.getBucketKey(), s3Service.downloadFile(resourceEntity.getBucketKey())))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found!"));
    }

    @Override
    public List<Integer> deleteByIds(String ids) {
        List<Integer> existingIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .filter(resourceRepository::existsById)
                .toList();
        resourceRepository.findAllById(existingIds).stream()
                .map(ResourceEntity::getBucketKey)
                .forEach(s3Service::deleteFile);
        resourceRepository.deleteAllByIdInBatch(existingIds);
        return existingIds;
    }

    @Override
    public boolean resourceExists(String id) {
        return resourceRepository.existsById(Integer.parseInt(id));
    }
}
