package songservice.exception;

public class SongAlreadyExists extends RuntimeException {

    public SongAlreadyExists(String message) {
        super(message);
    }
}
