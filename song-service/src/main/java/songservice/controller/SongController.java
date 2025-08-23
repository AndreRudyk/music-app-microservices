package songservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import request.song.SongMetadataRequest;
import response.song.SongMetadataResponse;
import songservice.converter.SongConverter;
import songservice.entity.SongMetadataEntity;
import response.song.DeleteSongResponse;
import songservice.service.SongService;
import validation.ValidIdsCsv;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v1/songs")
public class SongController {

    private final SongService songService;

    private final SongConverter songConverter;

    @PostMapping
    public ResponseEntity<SongMetadataResponse> saveSongMetadata(@Valid @RequestBody SongMetadataRequest request) {
        SongMetadataEntity songMetadata = songService.createSongMetadata(request);
        return ResponseEntity.ok(songConverter.convert(songMetadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongMetadataResponse> getSongMetadata(@PathVariable Integer id) {
        return ResponseEntity.ok(songConverter.convert(songService.getSongMetadata(id)));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> deleteSongMetadata(@RequestParam("id") @ValidIdsCsv(maxLength = 100) String ids) {
        List<Integer> deletedIds = songService.deleteSongMetadata(ids);
        return ResponseEntity.ok(DeleteSongResponse.builder().ids(deletedIds).build());
    }
}
