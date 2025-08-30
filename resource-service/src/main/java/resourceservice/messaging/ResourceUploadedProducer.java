package resourceservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceUploadedProducer {

    private final StreamBridge streamBridge;

    public void sendResourceId(String resourceId) {
        streamBridge.send("resourceUploaded-out-0", resourceId);
    }
}
