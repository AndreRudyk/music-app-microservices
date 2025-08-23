package resourceservice.service;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import request.song.SongMetadataRequest;

import java.io.IOException;

public interface CreateSongRequestService {

    SongMetadataRequest extractMetadata(byte[] songData, Integer id) throws IOException, SAXException, TikaException;
}
