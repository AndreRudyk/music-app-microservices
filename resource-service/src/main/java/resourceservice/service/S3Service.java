package resourceservice.service;

public interface S3Service {

    void uploadFile(String key, byte[] bytes, String bucketName);

    byte[] downloadFile(String key, String bucket);

    void deleteFile(String key, String bucket);

    String getFileUrl(String s3key, String bucketName);

    boolean bucketExists(String bucketName);

    void createBucket(String bucketName);
}
