package resourceservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"resourceservice.cucumber", "resourceservice.cucumber.stepdefs"},
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberRunnerTest {
    // This class serves as an entry point for Cucumber tests
}
