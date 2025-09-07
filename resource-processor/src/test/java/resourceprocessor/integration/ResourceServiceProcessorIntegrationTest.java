package resourceprocessor.integration;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import resourceprocessor.feign.ResourceServiceClient;
import resourceprocessor.feign.SongServiceClient;
import resourceprocessor.ResourceProcessorApplication;
import resourceprocessor.messaging.ResourceUploadedListener;
import request.song.SongMetadataRequest;
import response.song.SongMetadataResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {ResourceProcessorApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.stream.defaultBinder=rabbit",
        "spring.cloud.stream.bindings.resourceUploaded-in-0.destination=resource-uploaded",
        "spring.cloud.stream.bindings.resourceUploaded-in-0.group=resource-processor-group"
    }
)
@Testcontainers
public class ResourceServiceProcessorIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ResourceUploadedListener resourceUploadedListener;

    @MockitoBean
    private SongServiceClient songServiceClient;

    @MockitoBean
    private ResourceServiceClient resourceServiceClient;

    private static final String TEST_DATA_FOLDER = "src/test/resources/test-data/";

    @DynamicPropertySource
    static void rabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }

    @Test
    public void testResourceProcessingFlow() throws IOException {
       when(songServiceClient.saveSongMetadata(any(SongMetadataRequest.class)))
               .thenReturn(ResponseEntity.ok(getSongMetadataResponse()));
        Path tempMp3 = Files.createTempFile("test", ".mp3");
        byte[] mp3Data = Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + "valid-sample-with-required-tags.mp3"));
        Files.write(tempMp3, mp3Data);
        when(resourceServiceClient.getResource(anyString())).thenReturn(ResponseEntity.ok(mp3Data));


        resourceUploadedListener.resourceUploaded().accept("1");

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(songServiceClient, times(1)).saveSongMetadata(any(SongMetadataRequest.class));
        });
        verify(songServiceClient).saveSongMetadata(argThat(request ->
                Objects.equals(request.getId(), "1") &&
                        request.getName().contains("Test Title") &&
                        request.getArtist().equals("Test Artist") &&
                        "Test Album".equals(request.getAlbum())
        ));
        verify(resourceServiceClient).getResource("1");
    }

    @NotNull
    private static SongMetadataResponse getSongMetadataResponse() {
        return new SongMetadataResponse(1, "Test Title", "Test Artist", "Test Album",
                "00:07", "2025");
    }
}
