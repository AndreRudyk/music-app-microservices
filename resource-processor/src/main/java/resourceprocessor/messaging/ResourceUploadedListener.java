package resourceprocessor.messaging;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;
import request.song.SongMetadataRequest;
import resourceprocessor.feign.ResourceServiceClient;
import resourceprocessor.feign.SongServiceClient;
import resourceprocessor.service.CreateSongRequestService;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class ResourceUploadedListener {

    private final CreateSongRequestService createSongRequestService;

    private final SongServiceClient songServiceClient;

    private final ResourceServiceClient resourceServiceClient;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void handleResourceUploaded(String resourceId) throws TikaException, IOException, SAXException {
        log.info("Received a message with resourceId: {}", resourceId );
        ResponseEntity<byte[]> response = resourceServiceClient.getResource(String.valueOf(resourceId));
        byte[] resourceData = response.getBody();
        SongMetadataRequest request = createSongRequestService.extractMetadata(resourceData,  Integer.valueOf(resourceId));
        log.info("Before call song service with request: {}", request);
        songServiceClient.saveSongMetadata(request);
    }
}
