package resourceservice.client.feign;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import request.song.SongMetadataRequest;
import response.song.DeleteSongResponse;
import response.song.SongMetadataResponse;
import validation.ValidIdsCsv;

@FeignClient(name = "song-service")
public interface SongServiceClient {

    @PostMapping("/song-service/api/v1/songs")
    public ResponseEntity<SongMetadataResponse> saveSongMetadata(@Valid @RequestBody SongMetadataRequest request);

    @DeleteMapping("/song-service/api/v1/songs")
    public ResponseEntity<DeleteSongResponse> deleteSongMetadata(@RequestParam("id") @ValidIdsCsv(maxLength = 100) String ids);
}
