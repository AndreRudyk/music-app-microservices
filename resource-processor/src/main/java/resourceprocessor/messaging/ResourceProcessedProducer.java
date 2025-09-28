package resourceprocessor.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceProcessedProducer {

    private final StreamBridge streamBridge;

    public void sendProcessedResourceId(String resourceId) {
        streamBridge.send("resourceProcessed-out-0", resourceId);
    }
}
