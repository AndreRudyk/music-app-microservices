package songservice.service;

import request.song.SongMetadataRequest;
import songservice.entity.SongMetadataEntity;

import java.util.List;

public interface SongService {

    SongMetadataEntity createSongMetadata(SongMetadataRequest request);

    SongMetadataEntity getSongMetadata(Integer id);

    List<Integer> deleteSongMetadata(String ids);
}
