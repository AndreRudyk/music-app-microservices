package resourceservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import resourceservice.messaging.ResourceUploadedProducer;
import resourceservice.repository.ResourceRepository;
import resourceservice.service.S3Service;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
class
ResourceServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    @MockitoBean
    private S3Service s3Service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResourceUploadedProducer resourceUploadedProducer;

    private static final String TEST_DATA_FOLDER = "src/test/resources/test-data/";

    @Test
    void uploadResource_endpointSavesResourceAndReturnsId() throws Exception {
        when(s3Service.getFileUrl(anyString(), anyString())).thenReturn("http://mock-s3/file.mp3");
        doNothing().when(s3Service).uploadFile(anyString(), any(), anyString());
        doNothing().when(resourceUploadedProducer).sendResourceId(anyString());
        byte[] file = Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + "valid-sample-with-required-tags.mp3"));

        MvcResult result = mockMvc.perform(post("/api/v1/resources")
                        .contentType("audio/mpeg")
                        .content(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        Integer id = objectMapper.readTree(responseJson).get("id").asInt();
        assertThat(id).isNotNull();
        assertThat(resourceRepository.findById(id)).isPresent();
        verify(s3Service).uploadFile(anyString(), any(), anyString());
        verify(resourceUploadedProducer).sendResourceId(anyString());
    }

    @Test
    void uploadResourceWhenSongFileInvalid_returns400() throws Exception {
        when(s3Service.getFileUrl(anyString(), anyString())).thenReturn("http://mock-s3/file.mp3");
        doNothing().when(s3Service).uploadFile(anyString(), any(), anyString());
        doNothing().when(resourceUploadedProducer).sendResourceId(anyString());
        byte[] file = Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + "invalid-sample-with-missed-tags.mp3"));

        MvcResult result = mockMvc.perform(post("/api/v1/resources")
                        .contentType("audio/mpeg")
                        .content(file))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String errorMessage = objectMapper.readTree(responseJson).get("errorMessage").asText();
        assertThat(errorMessage).isEqualTo("uploadResource.file: Invalid file.");
        verifyNoInteractions(s3Service);
        verifyNoInteractions(resourceUploadedProducer);
    }
}
