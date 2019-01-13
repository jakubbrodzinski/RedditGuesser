package integration;

import bach.project.configuration.Application;
import bach.project.configuration.IntegrationTestingAdditionalConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ContextHierarchy({
        @ContextConfiguration(classes = IntegrationTestingAdditionalConfiguration.class),
        @ContextConfiguration(classes = Application.class)
})
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"integration-testing"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationMVCTesting {

}
