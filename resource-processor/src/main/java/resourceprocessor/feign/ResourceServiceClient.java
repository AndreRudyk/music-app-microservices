package resourceprocessor.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resource-service")
public interface ResourceServiceClient {

    @GetMapping(path = "resource-service/api/v1/resources/{id}/exists")
    ResponseEntity<Boolean> existsResource(@PathVariable String id);

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @GetMapping(path = "resource-service/api/v1/resources/{id}", produces = "audio/mpeg")
    ResponseEntity<byte[]> getResource(@PathVariable  String id);
}
