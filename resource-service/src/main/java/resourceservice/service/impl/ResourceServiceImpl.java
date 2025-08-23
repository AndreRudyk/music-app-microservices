package resourceservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import request.song.SongMetadataRequest;
import resourceservice.client.feign.SongServiceClient;
import resourceservice.entity.ResourceEntity;
import resourceservice.exception.ResourceNotFoundException;
import resourceservice.repository.ResourceRepository;
import resourceservice.service.CreateSongRequestService;
import resourceservice.service.ResourceService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    private final CreateSongRequestService createSongRequestService;

    private final SongServiceClient songServiceClient;

    @Override
    public ResourceEntity saveResource(byte[] file) {
        ResourceEntity savedResource = resourceRepository.save(new ResourceEntity(file));
        SongMetadataRequest request;
        try {
            request = createSongRequestService.extractMetadata(file, savedResource.getId());
        } catch (IOException | SAXException | TikaException e) {
            log.error("Exception occured while extracting metadata, resource id is {}", savedResource.getId());
            throw new RuntimeException(e);
        }
        try {
            songServiceClient.saveSongMetadata(request);
        } catch (Exception e) {
            resourceRepository.deleteById(savedResource.getId());
            throw new RuntimeException(e);
        }
        return savedResource;
    }

    @Override
    public ResourceEntity getResourceById(Integer id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found!"));
    }

    @Override
    public List<Integer> deleteByIds(String ids) {
        List<Integer> existingIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .filter(resourceRepository::existsById)
                .toList();
        resourceRepository.deleteAllByIdInBatch(existingIds);
        songServiceClient.deleteSongMetadata(ids);
        return existingIds;
    }

    @Override
    public boolean resourceExists(String id) {
        return resourceRepository.existsById(Integer.parseInt(id));
    }
}
