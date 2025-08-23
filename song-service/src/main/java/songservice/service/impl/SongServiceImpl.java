package songservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import request.song.SongMetadataRequest;
import songservice.client.feign.ResourceServiceClient;
import songservice.converter.SongConverter;
import songservice.entity.SongMetadataEntity;
import songservice.exception.SongAlreadyExists;
import songservice.exception.SongMetadataNotFound;
import songservice.repository.SongMetadataRepository;
import songservice.service.SongService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongMetadataRepository repository;

    private final SongConverter converter;

    private final ResourceServiceClient client;

    @Override
    public SongMetadataEntity createSongMetadata(SongMetadataRequest request) {
        if (repository.existsById(Integer.valueOf(request.getId()))) {
            log.error("Song with id {} already exists", request.getId());
            throw new SongAlreadyExists(String.format("Resource with ID=%s already exists", request.getId()));
        }
        return repository.save(converter.convert(request));
    }

    @Override
    public SongMetadataEntity getSongMetadata(Integer id) {
        if (Boolean.FALSE.equals(client.existsResource(String.valueOf(id)).getBody())) {
            log.error("Song not found, id: {}", id);
            repository.deleteAllByIdInBatch(List.of(id));
            throw new SongMetadataNotFound(String.format("Resource with ID=%s not found", id));
        }
        return repository.findById(id)
                .orElseThrow(() ->
                        new SongMetadataNotFound((String.format("Resource with ID=%s not found", id))));
    }

    @Override
    public List<Integer> deleteSongMetadata(String ids) {
        List<Integer> existingIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .filter(repository::existsById)
                .toList();
        repository.deleteAllByIdInBatch(existingIds);
        return existingIds;
    }
}
