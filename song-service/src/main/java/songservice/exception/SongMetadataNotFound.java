package songservice.exception;

public class SongMetadataNotFound extends RuntimeException {

    public SongMetadataNotFound(String message) {
        super(message);
    }
}
