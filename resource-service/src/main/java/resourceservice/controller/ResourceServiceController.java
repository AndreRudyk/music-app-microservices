package resourceservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import resourceservice.entity.ResourceEntity;
import resourceservice.service.ResourceService;
import resourceservice.validation.TagValidation;
import resourceservice.validation.PositiveInteger;
import response.resource.DeleteResourceResponse;
import response.resource.ResourceResponse;
import validation.ValidIdsCsv;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceServiceController {

    private final ResourceService resourceService;

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<ResourceResponse> uploadResource(@RequestBody @TagValidation byte[] file) {
        ResourceEntity uploadedEntity = resourceService.saveResource(file);
        return ResponseEntity.ok(new ResourceResponse(uploadedEntity.getId()));
    }

    @GetMapping(path = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable @Valid @PositiveInteger String id) {
        Pair<String, byte[]> resultPair = resourceService.getResourceById(Integer.parseInt(id));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ resultPair.getLeft() + "\"");
        httpHeaders.setContentLength(resultPair.getRight().length);
        httpHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(resultPair.getRight());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeleteResourceResponse> deleteResource(@RequestParam("id") @ValidIdsCsv(maxLength = 100) String ids) {
        List<Integer> deletedIds = resourceService.deleteByIds(ids);
        return ResponseEntity.ok(DeleteResourceResponse.builder().ids(deletedIds).build());
    }

    @GetMapping(path = "/{id}/exists")
    public ResponseEntity<Boolean> existsResource(@PathVariable @Valid @PositiveInteger String id) {
        return ResponseEntity.ok(resourceService.resourceExists(id));
    }
}
