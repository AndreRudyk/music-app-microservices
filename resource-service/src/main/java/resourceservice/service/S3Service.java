package resourceservice.service;

public interface S3Service {

    void uploadFile(String key, byte[] bytes);

    byte[] downloadFile(String key);

    void deleteFile(String key);

    String getFileUrl(String s3key);
}
