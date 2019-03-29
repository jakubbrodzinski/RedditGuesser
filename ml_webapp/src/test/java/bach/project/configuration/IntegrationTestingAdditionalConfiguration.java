package bach.project.configuration;

import bach.project.apis.CommentAPIConnector;
import bach.project.apis.RedditAPIConnector;
import bach.project.service.MailService;
import bach.project.apis.APIConnector;
import bean.DockerContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import net.dean.jraw.RedditClient;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public CommentAPIConnector commentAPIConnector(){
        CommentAPIConnector apiMock=Mockito.mock(RedditAPIConnector.class);
        Mockito.when(apiMock.getTitle(ArgumentMatchers.anyString())).thenReturn(StringUtils.randomAlphanumeric(15));
        Mockito.when(apiMock.getComments(ArgumentMatchers.anyString(),ArgumentMatchers.anyInt())).then(invocation -> {
            int amount=invocation.getArgument(1);
            return IntStream.range(0,amount).mapToObj(i->StringUtils.randomAlphanumeric(40)).collect(Collectors.toList());
        });
        return apiMock;
    }

    @Bean
    @Primary
    public MailService mailService() {
        Answer<Boolean> mockAnswer = (invocation) -> Boolean.TRUE;
        return Mockito.mock(MailService.class, mockAnswer);
    }

    @Bean
    @Primary
    public APIConnector apiConnector(){
        APIConnector apiConnectorMocked=Mockito.mock(APIConnector.class);
        Answer<byte[]> mockedAnswer=new Answer<byte[]>() {
            private final ObjectMapper objectMapper=new ObjectMapper();
            private final Random randomGen=new Random();

            @Override
            public byte[] answer(InvocationOnMock invocation) throws Throwable {
                List<AbstractMap.SimpleEntry<String, String>> comments=invocation.getArgument(0);
                ObjectNode mainObjectNode=objectMapper.createObjectNode();
                comments.stream().map(entry->entry.getKey()).forEach(k->{
                    ObjectNode apiResponse=objectMapper.createObjectNode();
                    apiResponse.put("score",randomGen.nextInt(150)+1);
                    apiResponse.put("shouldBeRemoved",randomGen.nextBoolean());
                    mainObjectNode.set(k,apiResponse);
                });
                return mainObjectNode.toString().getBytes();
            }
        };

        Mockito.when(apiConnectorMocked.sendRequestToAPI(ArgumentMatchers.any(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).then(mockedAnswer);
        return apiConnectorMocked;
    }
}
