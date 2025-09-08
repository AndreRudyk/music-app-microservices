package resourceservice.cucumber.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import resourceservice.cucumber.CucumberSpringConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceUploadStepDefs extends CucumberSpringConfiguration {

    private static final String TEST_DATA_FOLDER = "src/test/resources/test-data/";
    private static final String QUEUE_NAME = "resource-uploaded";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalStackContainer localStackContainer;

    private MvcResult uploadResult;
    private Integer resourceId;
    private Message receivedMessage;

    @Before
    public void setUp() {
        // Clear the queue before each scenario
        amqpAdmin.purgeQueue(QUEUE_NAME, true);
    }

    @Given("the resource service is running with RabbitMQ")
    public void theResourceServiceIsRunningWithRabbitMQ() {
        // Verify that our test infrastructure is running
        assertThat(rabbitTemplate).isNotNull();
        assertThat(amqpAdmin).isNotNull();
        assertThat(localStackContainer.isRunning()).isTrue();
        System.out.println("Resource service is running with RabbitMQ and LocalStack S3");
    }

    @When("I upload a valid audio file")
    public void iUploadAValidAudioFile() throws Exception {
        byte[] file = Files.readAllBytes(Paths.get(TEST_DATA_FOLDER + "valid-sample-with-required-tags.mp3"));

        uploadResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/resources")
                .contentType("audio/mpeg")
                .content(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = uploadResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        resourceId = jsonNode.get("id").asInt();

        System.out.println("Resource uploaded with ID: " + resourceId);
    }

    @Then("the resource should be saved successfully")
    public void theResourceShouldBeSavedSuccessfully() {
        assertThat(resourceId).isNotNull();
        assertThat(resourceId).isGreaterThan(0);
    }

    @Then("a message with the resource ID should be published to RabbitMQ")
    public void aMessageWithTheResourceIdShouldBePublishedToRabbitMQ() throws Exception {
        // Configure receive timeout
        rabbitTemplate.setReceiveTimeout(5000);

        // Receive message from queue
        receivedMessage = rabbitTemplate.receive(QUEUE_NAME);
        assertThat(receivedMessage).isNotNull();

        String messageBody = new String(receivedMessage.getBody());
        assertThat(messageBody).isEqualTo(resourceId.toString());

        System.out.println("Message received from RabbitMQ: " + messageBody);
    }

    @Then("the resource processor should be able to retrieve the resource")
    public void theResourceProcessorShouldBeAbleToRetrieveTheResource() throws Exception {
        // Simulate the resource-processor retrieving the resource
        MvcResult getResourceResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/resources/{id}", resourceId)
                .accept("audio/mpeg"))
                .andExpect(status().isOk())
                .andReturn();

        // Verify that we received the resource data
        byte[] resourceData = getResourceResult.getResponse().getContentAsByteArray();
        assertThat(resourceData).isNotEmpty();

        System.out.println("Resource processor successfully retrieved resource data");
    }
}
