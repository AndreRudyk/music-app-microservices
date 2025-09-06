package songservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import songservice.client.feign.ResourceServiceClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SongControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceServiceClient client;

    private final String requestBody = """
            {
              "id": 1,
              "name": "Test Song",
              "artist": "Test Artist",
              "album": "Test Album",
              "duration": "01:30",
              "year": 2020
            }
            """;

    @Test
    void uploadSongMetadata_shouldSaveAndReturnMetadata() throws Exception {
        mockMvc.perform(post("/api/v1/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Song"));
    }

    @Test
    void getSongMetadata_shouldReturnMetadata() throws Exception {
        when(client.existsResource(anyString())).thenReturn(ResponseEntity.ok(true));

        String response = mockMvc.perform(post("/api/v1/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/v1/songs/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(id)))
                .andExpect(jsonPath("$.name").value("Test Song"));
    }

    @Test
    void deleteSongMetadata_shouldDeleteMetadata() throws Exception {
        when(client.existsResource(anyString())).thenReturn(ResponseEntity.ok(true));

        String response = mockMvc.perform(post("/api/v1/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/v1/songs?id=" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(Integer.parseInt(id)));
    }

    @Test
    void getSongMetadataWhenSongNonExistent_returns404() throws Exception {
        when(client.existsResource(anyString())).thenReturn(ResponseEntity.ok(false));

        mockMvc.perform(get("/api/v1/songs/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource with ID=1 not found"))
                .andExpect(jsonPath("$.errorCode").value(404));
    }
}
