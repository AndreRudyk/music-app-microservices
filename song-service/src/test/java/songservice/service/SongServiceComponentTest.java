package songservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import request.song.SongMetadataRequest;
import songservice.client.feign.ResourceServiceClient;
import songservice.entity.SongMetadataEntity;
import songservice.repository.SongMetadataRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SongServiceComponentTest {

    @Autowired
    private SongService songService;

    @Autowired
    private SongMetadataRepository repository;

    @MockitoBean
    private ResourceServiceClient resourceServiceClient;

    @Test
    void createSongMetadata_savesToDatabase() {
        SongMetadataRequest request = new SongMetadataRequest(
                "1", "Test Song", "Test Artist", "Test Album", "03:30", "2020");

        SongMetadataEntity entity = songService.createSongMetadata(request);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("Test Song");
        assertThat(repository.findById(1)).isPresent();
    }

    @Test
    void getSongMetadata_returnsEntity_whenResourceExists() {
        SongMetadataEntity entity = new SongMetadataEntity(2, "Song2", "Artist2", "Album2", "04:00", "2021");
        repository.save(entity);
        when(resourceServiceClient.existsResource(anyString())).thenReturn(org.springframework.http.ResponseEntity.ok(true));

        SongMetadataEntity found = songService.getSongMetadata(2);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(2);
    }

    @Test
    void getSongMetadata_throwsException_whenResourceDoesNotExist() {
        SongMetadataEntity entity = new SongMetadataEntity(3, "Song3", "Artist3", "Album3", "05:00", "2022");
        repository.save(entity);

        when(resourceServiceClient.existsResource(anyString())).thenReturn(org.springframework.http.ResponseEntity.ok(false));

        assertThrows(songservice.exception.SongMetadataNotFound.class, () -> songService.getSongMetadata(3));
    }
}
