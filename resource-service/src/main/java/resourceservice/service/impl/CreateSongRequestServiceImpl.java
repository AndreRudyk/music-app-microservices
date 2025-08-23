package resourceservice.service.impl;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import request.song.SongMetadataRequest;
import resourceservice.service.CreateSongRequestService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static resourceservice.util.AudioTagUtils.*;

@Service
public class CreateSongRequestServiceImpl implements CreateSongRequestService {

    @Override
    public SongMetadataRequest extractMetadata(byte[] songData, Integer id) throws IOException, TikaException, SAXException {
        try (InputStream inputstream = new ByteArrayInputStream(songData)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputstream, handler, metadata, pcontext);

            SongMetadataRequest songMetadata = new SongMetadataRequest();
            songMetadata.setId(String.valueOf(id));
            songMetadata.setName(metadata.get(TITLE_TAG) != null ? metadata.get(TITLE_TAG) : "Unknown Title");
            songMetadata.setArtist(metadata.get(ARTIST_TAG) != null ? metadata.get(ARTIST_TAG) : "Unknown Artist");
            songMetadata.setAlbum(metadata.get(ALBUM_TAG) != null ? metadata.get(ALBUM_TAG) : "Unknown Album");
            songMetadata.setDuration(formatDuration(metadata.get(DURATION_TAG)));
            songMetadata.setYear(metadata.get(RELEASE_DATE_TAG));

            return songMetadata;
        }
    }

    private String formatDuration(String rawDuration) {
        if (rawDuration != null && !rawDuration.isEmpty()) {
            try {
                double durationSec = Double.parseDouble(rawDuration);
                int minutes = (int) (durationSec / 60);
                int seconds = (int) (durationSec % 60);
                return String.format("%02d:%02d", minutes, seconds);
            } catch (NumberFormatException e) {
                System.err.println("Invalid duration format");
            }
        }
        return "00:00";
    }
}
