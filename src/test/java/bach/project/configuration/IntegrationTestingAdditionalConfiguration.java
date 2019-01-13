package bach.project.configuration;

import bach.project.service.MailService;
import bean.DockerContainer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import net.dean.jraw.RedditClient;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.util.function.Function;

public class IntegrationTestingAdditionalConfiguration {
    @Inject
    private Environment environment;

    @Bean
    public DockerClient dockerClient() {
        return DockerClientBuilder.getInstance(
                DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost("unix:///var/run/docker.sock")
                        .withDockerTlsVerify(false)
                        .withApiVersion("1.39")
                        .build())
                .build();
    }

    @Bean
    public DockerContainer mongoContainer(DockerClient dockerClient){
        final String userName=environment.getProperty("spring.data.mongodb.username","root");
        final String password= environment.getProperty("spring.data.mongodb.password","integration_testing");
        final Integer port= environment.getProperty("spring.data.mongodb.port",Integer.class,Integer.valueOf(27017));

        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(27017), Ports.Binding.bindPort(port));

        Function<DockerClient, CreateContainerResponse> createContainerResponseFunction=(dClient ->
                dClient.createContainerCmd("mongo")
                .withEnv("MONGO_INITDB_ROOT_USERNAME="+userName,"MONGO_INITDB_ROOT_PASSWORD="+password)
                .withPortBindings(portBindings)
                .withCmd("--smallfiles")
                .withRestartPolicy(RestartPolicy.alwaysRestart())
                .exec()
        );

        return new DockerContainer(dockerClient,createContainerResponseFunction);
    }

    @Bean
    @Primary
    public RedditClient redditClient() {
        RedditClient mock = Mockito.mock(RedditClient.class);
        return mock;
    }

    @Bean
    @Primary
    public MailService mailService() {
        Answer<Boolean> mockAnswer = (invocation) -> Boolean.TRUE;
        return Mockito.mock(MailService.class, mockAnswer);
    }
}
