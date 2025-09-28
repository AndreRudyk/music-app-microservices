package resourceprocessor.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import request.song.SongMetadataRequest;
import resourceprocessor.feign.ResourceServiceClient;
import resourceprocessor.feign.SongServiceClient;
import resourceprocessor.service.CreateSongRequestService;
import java.util.function.Consumer;

@Slf4j
@Component
@AllArgsConstructor
public class ResourceUploadedListener {

    private final CreateSongRequestService createSongRequestService;

    private final SongServiceClient songServiceClient;

    private final ResourceServiceClient resourceServiceClient;

    private final ResourceProcessedProducer resourceProcessedProducer;

    @Bean
    public Consumer<String> resourceUploaded() {
        return resourceId -> {
            log.info("Received a message with resourceId: {}", resourceId);
            ResponseEntity<byte[]> response = resourceServiceClient.getResource(resourceId);
            byte[] resourceData = response.getBody();
            SongMetadataRequest request =
                    createSongRequestService.extractMetadata(resourceData, Integer.parseInt(resourceId));
            log.info("Before calling song service with request: {}", request);
            songServiceClient.saveSongMetadata(request);
            log.info("Publishing processed resource ID: {}", resourceId);
            resourceProcessedProducer.sendProcessedResourceId(resourceId);
        };
    }
}
