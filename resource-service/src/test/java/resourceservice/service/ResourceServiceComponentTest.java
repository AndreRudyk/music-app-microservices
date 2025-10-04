package resourceservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import resourceservice.entity.ResourceEntity;
import resourceservice.repository.ResourceRepository;
import resourceservice.messaging.ResourceUploadedProducer;
import resourceservice.feign.SongServiceClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ResourceServiceComponentTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @MockitoBean
    private S3Service s3Service;

    @MockitoBean
    private ResourceUploadedProducer resourceUploadedProducer;

    @MockitoBean
    private SongServiceClient songServiceClient;

    @Test
    void saveResource_ToStaging_savesEntityToDatabase() {
        byte[] dummyFile = new byte[]{1, 2, 3};
        String dummyUrl = "http://dummy-url";
        when(s3Service.getFileUrl(any(), any())).thenReturn(dummyUrl);

        ResourceEntity saved = resourceService.saveResourceToStaging(dummyFile);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBucketKey()).isNotNull();
        assertThat(saved.getFileUrl()).isEqualTo(dummyUrl);

        Optional<ResourceEntity> fromDb = resourceRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getBucketKey()).isEqualTo(saved.getBucketKey());
        assertThat(fromDb.get().getFileUrl()).isEqualTo(saved.getFileUrl());
    }

    @Test
    void resourceExists_returnsTrueForExistingResource() {
        byte[] dummyFile = new byte[]{4, 5, 6};
        String dummyUrl = "http://dummy-url-2";
        when(s3Service.getFileUrl(any(), any())).thenReturn(dummyUrl);
        ResourceEntity saved = resourceService.saveResourceToStaging(dummyFile);

        boolean exists = resourceService.resourceExists(saved.getId().toString());

        assertThat(exists).isTrue();
    }

    @Test
    void deleteByIds_deletesExistingResource() {
        byte[] dummyFile = new byte[]{4, 5, 6};
        String dummyUrl = "http://dummy-url-2";
        when(s3Service.getFileUrl(any(), any())).thenReturn(dummyUrl);

        ResourceEntity saved = resourceService.saveResourceToStaging(dummyFile);

        boolean exists = resourceService.resourceExists(saved.getId().toString());
        assertThat(exists).isTrue();

        resourceService.deleteByIds(saved.getId().toString(), "staging-bucket");
        boolean result = resourceService.resourceExists(saved.getId().toString());

        assertThat(result).isFalse();
    }
}
