package resourceservice.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import resourceservice.configs.AwsProperties;
import resourceservice.service.S3Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    private final AwsProperties awsProperties;

    @Override
    public void uploadFile(String key, byte[] bytes, String bucketName) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));
    }

    @Override
    public byte[] downloadFile(String key, String bucketName) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()).readAllBytes();
        } catch (IOException e) {
            log.error("Error occurred: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String key, String bucketName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    @Override
    public String getFileUrl(String s3key, String bucketName) {
        return awsProperties.getEndpoint() + "/" + bucketName + "/" + s3key;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(request -> request.bucket(bucketName));
            return true;
        } catch (NoSuchBucketException exception) {
            return false;
        }
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ex) {
            log.warn("Bucket already exists. Message: {}", ex.getMessage());
        }
    }
}
