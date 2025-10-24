package resourceprocessor.messaging;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import request.song.SongMetadataRequest;
import resourceprocessor.feign.ResourceServiceClient;
import resourceprocessor.feign.SongServiceClient;
import resourceprocessor.service.CreateSongRequestService;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(MockitoExtension.class)
@PactTestFor(providerName = "resource-service-queue", providerType = ProviderType.ASYNCH)
public class ResourceUploadedListenerContractTest {

    private static final String RESOURCE_ID = "42";

    private static final byte[] SAMPLE_RESOURCE_DATA = "mock audio data".getBytes();

    @Mock
    private CreateSongRequestService createSongRequestService;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private ResourceServiceClient resourceServiceClient;

    @Mock
    private ResourceProcessedProducer resourceProcessedProducer;

    @InjectMocks
    private ResourceUploadedListener resourceUploadedListener;

    @Pact(consumer = "resource-processor", provider = "resource-service")
    public V4Pact resourceUploadedMessagePact(PactBuilder builder) {
        return builder
                .usingLegacyMessageDsl()
                .expectsToReceive("a resource uploaded event")
                .withContent(RESOURCE_ID)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "resourceUploadedMessagePact")
    void testResourceUploadedProcessing(List<V4Interaction.AsynchronousMessage> messages) {
        String message = new String(messages.get(0).contentsAsBytes());
        SongMetadataRequest mockRequest = new SongMetadataRequest();
        mockRequest.setId(RESOURCE_ID);
        ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(SAMPLE_RESOURCE_DATA, HttpStatus.OK);
        when(resourceServiceClient.getResource(RESOURCE_ID)).thenReturn(mockResponse);
        when(createSongRequestService.extractMetadata(SAMPLE_RESOURCE_DATA, Integer.parseInt(RESOURCE_ID)))
                .thenReturn(mockRequest);

        resourceUploadedListener.resourceUploaded().accept(message);

        verify(resourceServiceClient).getResource(RESOURCE_ID);
        verify(createSongRequestService).extractMetadata(SAMPLE_RESOURCE_DATA, Integer.parseInt(RESOURCE_ID));
        verify(songServiceClient).saveSongMetadata(mockRequest);
    }
}
