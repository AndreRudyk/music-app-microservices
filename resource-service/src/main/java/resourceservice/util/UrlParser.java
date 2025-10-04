package resourceservice.util;

import java.net.MalformedURLException;
import java.net.URL;

public final class UrlParser {

    private UrlParser() { }

    /**
     * Extracts the bucket name from a URL string. The bucket name is assumed to be
     * the first path segment immediately following the host and port.
     *
     * @param urlString The full URL string, e.g., "http://localstack:4566/file-bucket/file-key.mp3".
     * @return The extracted bucket name (e.g., "file-bucket"), or null if parsing fails
     * or no path segment is found.
     */
    public static String extractBucketName(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            System.err.println("Error: URL string cannot be null or empty.");
            return null;
        }
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            if (path == null || path.length() <= 1) {
                System.out.println("No path or bucket name found in the URL.");
                return null;
            }
            String[] segments = path.split("/");
            if (segments.length > 1) {
                return segments[1];
            }
        } catch (MalformedURLException e) {
            System.err.println("Error: The provided URL is malformed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
        return null;
    }
}
