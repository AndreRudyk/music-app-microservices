package songservice.client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "resource-service")
public class ResourceServiceClientContractTest {

    private final String RESOURCE_ID = "123e4567-e89b-12d3-a456-426614174000";

    @Pact(consumer = "song-service")
    public V4Pact resourceExistsWhenResourceExists(PactBuilder builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
                .usingLegacyDsl()
                .given("Resource exists with ID " + RESOURCE_ID)
                .uponReceiving("A request to check if resource exists")
                .path("/resource-service/api/v1/resources/" + RESOURCE_ID + "/exists")
                .method("GET")
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(headers)
                .body("true")
                .toPact(V4Pact.class);
    }

    @Pact(consumer = "song-service")
    public V4Pact resourceDoesNotExistWhenResourceDoesNotExist(PactBuilder builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
                .usingLegacyDsl()
                .given("Resource does not exist with ID " + RESOURCE_ID)
                .uponReceiving("A request to check if non-existent resource exists")
                .path("/resource-service/api/v1/resources/" + RESOURCE_ID + "/exists")
                .method("GET")
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(headers)
                .body("false")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "resourceExistsWhenResourceExists")
    void testResourceExists(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/resource-service/api/v1/resources/" + RESOURCE_ID + "/exists";

        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @PactTestFor(pactMethod = "resourceDoesNotExistWhenResourceDoesNotExist")
    void testResourceDoesNotExist(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockServer.getUrl() + "/resource-service/api/v1/resources/" + RESOURCE_ID + "/exists";

        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }
}
