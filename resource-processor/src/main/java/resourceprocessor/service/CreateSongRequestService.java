package resourceprocessor.service;

import request.song.SongMetadataRequest;

public interface CreateSongRequestService {

    SongMetadataRequest extractMetadata(byte[] songData, Integer id);
}
