package extensions;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class DockerExtension implements TestInstancePostProcessor, AfterAllCallback {
    private final static String DOCKER_KEY="dockerClient";
    private final static String CONTAINER_KEY="containerId";
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        DockerClient dockerClient= getDockerClient(extensionContext);
        createAndStartContainer(dockerClient,extensionContext);
    }

    public DockerClient getDockerClient(ExtensionContext extensionContext){
        return extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent(DOCKER_KEY,k->{
            if(!k.equals(DOCKER_KEY))
                return null;
            return DockerClientBuilder.getInstance(
                        DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost("unix:///var/run/docker.sock")
                        .withDockerTlsVerify(false)
                        .withApiVersion("1.39")
                        .build())
                    .build();
        },DockerClient.class);
    }

    public void createAndStartContainer(DockerClient dockerClient,ExtensionContext extensionContext){
        ExposedPort tcpMongo = ExposedPort.tcp(27017);
        Ports portBindings = new Ports();
        portBindings.bind(tcpMongo, Ports.Binding.bindPort(27016));
/*        HealthCheck option will be released in next Docker-java/docker-client release :<
        HealthCheck mongoHealthCheck=new HealthCheck().withRetries(5).withInterval(1000000L).withTimeout(10000L).withTest(Collections.singletonList("echo 'db.stats().ok' | mongo localhost:27017/ --quiet"));*/

        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("mongo")
                .withEnv("MONGO_INITDB_ROOT_USERNAME=root", "MONGO_INITDB_ROOT_PASSWORD=integration_testing")
                .withPortBindings(portBindings)
                .withCmd("--smallfiles")
                .withRestartPolicy(RestartPolicy.alwaysRestart())
                .exec();

        dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put(CONTAINER_KEY,createContainerResponse.getId());
    }

    public void stopAndRemoveContainer(DockerClient dockerClient,ExtensionContext extensionContext){
        String containerId=extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).get(CONTAINER_KEY,String.class);
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        DockerClient dockerClient = getDockerClient(extensionContext);

        stopAndRemoveContainer(dockerClient,extensionContext);
        dockerClient.close();
    }
}
