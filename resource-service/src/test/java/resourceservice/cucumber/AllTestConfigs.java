package resourceservice.cucumber;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import resourceservice.cucumber.config.LocalStackConfig;

/**
 * This configuration class explicitly imports all test configurations
 * to ensure Spring can find and load them properly.
 */
@Configuration
@Import({CucumberTestConfig.class, LocalStackConfig.class})
public class AllTestConfigs {
    // This class serves as an aggregator for all test configurations
}

