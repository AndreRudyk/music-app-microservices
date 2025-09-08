package resourceservice.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import resourceservice.ResourceServiceApplication;

@CucumberContextConfiguration
@SpringBootTest(
        classes = {ResourceServiceApplication.class, AllTestConfigs.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.stream.default-binder=rabbit",
        "spring.cloud.stream.function.definition=resourceUploaded",
        "spring.cloud.stream.bindings.resourceUploaded-out-0.destination=resource-uploaded",
        "spring.cloud.stream.rabbit.bindings.resourceUploaded-out-0.producer.routing-key-expression='resource-uploaded'",
        "spring.cloud.function.autodetect=false"
})
public class CucumberSpringConfiguration {
    // This class configures the Spring context for Cucumber tests using the recommended class name
}
