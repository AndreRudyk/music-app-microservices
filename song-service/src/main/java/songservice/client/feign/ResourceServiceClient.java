package songservice.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resource-service")
public interface ResourceServiceClient {

    @GetMapping(path = "/resource-service/api/v1/resources/{id}/exists")
    public ResponseEntity<Boolean> existsResource(@PathVariable String id);
}
