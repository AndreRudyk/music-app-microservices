package songservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import request.song.SongMetadataRequest;
import songservice.client.feign.ResourceServiceClient;
import songservice.converter.SongConverter;
import songservice.entity.SongMetadataEntity;
import songservice.exception.SongAlreadyExists;
import songservice.exception.SongMetadataNotFound;
import songservice.repository.SongMetadataRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock
    private SongMetadataRepository repository;

    @Mock
    private SongConverter converter;

    @Mock
    private ResourceServiceClient client;

    @InjectMocks
    private SongServiceImpl songService;

    @Test
    void createSongMetadata_shouldSucceed() {
        Integer id = new Random().nextInt();
        SongMetadataRequest request = new SongMetadataRequest();
        request.setId(String.valueOf(id));
        when(repository.existsById(id)).thenReturn(false);
        SongMetadataEntity songMetadata = new SongMetadataEntity();
        when(converter.convert(request)).thenReturn(songMetadata);
        when(repository.save(songMetadata)).thenReturn(songMetadata);

        SongMetadataEntity result = songService.createSongMetadata(request);

        verify(repository).existsById(id);
        verify(converter).convert(request);
        verify(repository).save(songMetadata);
    }

    @Test
    void createSongMetadataWhenSongExists_throwsException() {
        Integer id = new Random().nextInt();
        SongMetadataRequest request = new SongMetadataRequest();
        request.setId(String.valueOf(id));
        when(repository.existsById(id)).thenReturn(true);

        assertThrows(SongAlreadyExists.class, () -> songService.createSongMetadata(request));

        verify(repository).existsById(id);
    }

    @Test
    void getSongMetadata_retrievesSongMetadataEntity() {
        ResponseEntity<Boolean> responseEntity =
                new ResponseEntity<>(true, HttpStatusCode.valueOf(200));
        when(client.existsResource("1")).thenReturn(responseEntity);
        SongMetadataEntity songMetadata = new SongMetadataEntity();
        when(repository.findById(1)).thenReturn(Optional.of(songMetadata));

        SongMetadataEntity result = songService.getSongMetadata(1);

        assertEquals(songMetadata, result);
        verify(client).existsResource("1");
        verify(repository).findById(1);
    }

    @Test
    void getSongMetadataWhenResourceNotExist_throwsException() {
        ResponseEntity<Boolean> responseEntity =
                new ResponseEntity<>(false, HttpStatusCode.valueOf(200));
        when(client.existsResource("1")).thenReturn(responseEntity);
        doNothing().when(repository).deleteAllByIdInBatch(List.of(1));

        assertThrows(SongMetadataNotFound.class, () -> songService.getSongMetadata(1));

        verify(client).existsResource("1");
        verify(repository).deleteAllByIdInBatch(List.of(1));
    }

    @Test
    void getSongMetadataWhenSongNotExist_throwsException() {
        ResponseEntity<Boolean> responseEntity =
                new ResponseEntity<>(true, HttpStatusCode.valueOf(200));
        when(client.existsResource("1")).thenReturn(responseEntity);
        SongMetadataEntity songMetadata = new SongMetadataEntity();
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(SongMetadataNotFound.class, () -> songService.getSongMetadata(1));

        verify(client).existsResource("1");
        verify(repository).findById(1);
    }

    @Test
    void deleteSongMetadata_returnsDeletedIds() {
        String ids = "1,2";
        when(repository.existsById(1)).thenReturn(true);
        when(repository.existsById(2)).thenReturn(true);
        doNothing().when(repository).deleteAllByIdInBatch(List.of(1, 2));

        List<Integer> results = songService.deleteSongMetadata(ids);

        assertEquals(List.of(1, 2), results);
        verify(repository).existsById(1);
        verify(repository).existsById(2);
        verify(repository).deleteAllByIdInBatch(List.of(1, 2));
    }
}
