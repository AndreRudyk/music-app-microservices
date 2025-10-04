package resourceservice.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import resourceservice.entity.StorageType;
import resourceservice.service.ResourceService;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceProcessedListener {

    private final ResourceService resourceService;

    @Bean
    public Consumer<String> resourceProcessed() {
        return  resourceId -> {
          log.info("Receive processed resource notification for resourceId: {}", resourceId);
            resourceService.updateStorageTypeByResourceId(resourceId, StorageType.PERMANENT);
        };
    }
}
