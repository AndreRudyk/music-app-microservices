package songservice.converter;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import response.song.SongMetadataResponse;
import songservice.entity.SongMetadataEntity;
import request.song.SongMetadataRequest;

@Mapper(componentModel = "spring")
public interface SongConverter {

    @Mapping(source = "year", target = "releaseYear")
    SongMetadataEntity convert(SongMetadataRequest request);

    @Mapping(source = "releaseYear", target = "year")
    SongMetadataResponse convert(SongMetadataEntity entity);
}
